package com.imures.kaadbackend.contact.service;

import com.imures.kaadbackend.configuration.MailService;
import com.imures.kaadbackend.configuration.PhoneNumberConverter;
import com.imures.kaadbackend.contact.controller.request.ContactRequest;
import com.imures.kaadbackend.contact.controller.response.ContactResponse;
import com.imures.kaadbackend.contact.controller.response.ContactTypeResponse;
import com.imures.kaadbackend.contact.entity.Contact;
import com.imures.kaadbackend.contact.entity.ContactType;
import com.imures.kaadbackend.contact.mapper.ContactMapper;
import com.imures.kaadbackend.contact.repository.ContactRepository;
import com.imures.kaadbackend.contact.repository.ContactTypeRepository;
import com.imures.kaadbackend.language.entity.Language;
import com.imures.kaadbackend.language.repository.LanguageRepository;
import com.imures.kaadbackend.specialization.entity.Specialization;
import com.imures.kaadbackend.specialization.repository.SpecializationRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ContactService {

    @Value("${application.contact.creationTimeout}")
    private Integer contactCreationTimeout;

    private final ContactRepository contactRepository;
    private final ContactMapper contactMapper;
    private final ContactTypeRepository contactTypeRepository;
    private final SpecializationRepository specializationRepository;
    private final LanguageRepository languageRepository;
    private final MailService mailService;



    @Transactional(readOnly = true)
    public List<ContactResponse> getAllContacts() {
        return contactRepository
                .findAll()
                .stream().map(contactMapper::fromEntityToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ContactResponse getContactById(Long id) {
        return  contactMapper
                .fromEntityToResponse(
                        contactRepository.findById(id)
                                .orElseThrow(
                                        ()-> new EntityNotFoundException(String.format("Contact with id: %s not found", id))
                                )
                );
    }

    @Transactional(readOnly = true)
    public Page<ContactResponse> findAll(Pageable pageRequest) {
        Page<Contact> contacts = contactRepository.findAll(pageRequest);
        return contacts.map(contactMapper::fromEntityToResponse);
    }

    @Transactional(readOnly = true)
    public Page<ContactResponse> findAllWithContactTypes(Pageable pageRequest, String types) {
        Set<Long> typesId = Set.of(
                Arrays.stream(
                        types.split(","))
                        .map(Long::parseLong)
                        .toArray(Long[]::new)
        );
        Page<Contact> contacts = contactRepository.findALlByContactType_IdIn(pageRequest, typesId);
        return contacts.map(contactMapper::fromEntityToResponse);
    }

    @Transactional
    public ContactResponse createContact(ContactRequest contactRequest) throws BadRequestException {
        if(contactRequest.getEmail() == null && contactRequest.getPhoneNumber() == null){
            throw new BadRequestException("New contact form could not be created if mail or phone is missing");
        }
        OffsetDateTime createdAt = OffsetDateTime.now();
        String key = generateKey(
                contactRequest.getFullName(),
                Optional.ofNullable(contactRequest.getPhoneNumber()).orElse(""),
                Optional.ofNullable(contactRequest.getEmail()).orElse(""),
                createdAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        );

        Optional<Contact> result = contactRepository.findLatestByUUID(key);
        if(!isPossibleToCreate(result) && result.isPresent()){
            return contactMapper.fromEntityToResponse(result.get());
        }

        ContactType contactType = contactTypeRepository.findById(contactRequest.getContactTypeId())
                .orElseThrow(()-> new EntityNotFoundException(String.format("Contact type with id: %s not found", key)));

        Specialization specialization = specializationRepository.findById(contactRequest.getSpecializationId())
                .orElseThrow(()-> new EntityNotFoundException(String.format("Specialization with id: %s not found", key)));

        Language language = languageRepository.findByCode(contactRequest.getLanguageCode())
                .orElseThrow(() -> new EntityNotFoundException(String.format("Language with code: %s, not found", contactRequest.getLanguageCode())));

        Contact contact = contactMapper.fromRequestToEntity(contactRequest);
        contact.setPhoneNumber(
                Optional.ofNullable(contactRequest.getPhoneNumber())
                        .map(PhoneNumberConverter::convertToCompactFormat)
                        .orElse(null)
        );
        contact.setCreatedAt(createdAt);
        contact.setResolved(false);
        contact.setUUID(key);
        contact.setContactType(contactType);
        contact.setSpecialization(specialization);
        contact.setLanguage(language);
        ContactResponse response = contactMapper.fromEntityToResponse(contactRepository.save(contact));

        mailService.sendContactEmail(response);

        return response;
    }

    private boolean isPossibleToCreate(Optional<Contact> result) {
        if(result.isEmpty()){
            return true;
        }
        Duration duration = Duration.between(result.get().getCreatedAt(), OffsetDateTime.now());

        return Math.abs(duration.toMinutes()) > contactCreationTimeout;
    }

    public static String generateKey(String fullName, String phoneNumber, String email, String date) {
        String data = fullName + phoneNumber + email + date;

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data.getBytes(StandardCharsets.UTF_8));

            StringBuilder hexString = new StringBuilder(2 * hash.length);
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error generating idempotency key", e);
        }
    }

    @Transactional
    public ContactResponse updateContact(ContactRequest contactRequest, Long contactId) {
        Contact contact = contactRepository.findById(contactId).orElseThrow(
                () -> new EntityNotFoundException(String.format("Contact with id %s not found", contactId))
        );
        Optional.ofNullable(contactRequest.getFullName()).ifPresent(contact::setFullName);
        Optional.ofNullable(contactRequest.getPhoneNumber()).ifPresent(contact::setPhoneNumber);
        Optional.ofNullable(contactRequest.getEmail()).ifPresent(contact::setEmail);
//        Optional.ofNullable(contactRequest.getReason()).ifPresent(contact::setReason);
        Optional.ofNullable(contactRequest.getContactTypeId()).ifPresent(
                aLong -> {
                    ContactType ct = contactTypeRepository.findById(aLong)
                            .orElseThrow(
                                    () -> new EntityNotFoundException(String.format("Contact Type with id: %s not found", aLong))
                            );
                    contact.setContactType(ct);
                }
        );

        return contactMapper.fromEntityToResponse(contact);
    }

    @Transactional
    public void deleteContact(Long contactId) {
        Contact contact = contactRepository.findById(contactId).orElseThrow(
                () -> new EntityNotFoundException(String.format("Contact with id %s not found", contactId))
        );
        contactRepository.delete(contact);
    }

    public List<ContactTypeResponse> getContactTypes() {
        List<ContactType> contactTypes = contactTypeRepository.findAll();
        List<ContactTypeResponse> contactTypesResponse = new ArrayList<>();
        for (ContactType contactType : contactTypes) {
            ContactTypeResponse contactTypeResponse = new ContactTypeResponse();
            contactTypeResponse.setId(contactType.getId());
            contactTypeResponse.setContactType(contactType.getContactType());
            contactTypesResponse.add(contactTypeResponse);
        }
        return contactTypesResponse;
    }
}
