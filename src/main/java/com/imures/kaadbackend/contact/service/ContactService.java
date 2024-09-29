package com.imures.kaadbackend.contact.service;

import com.imures.kaadbackend.contact.controller.request.ContactRequest;
import com.imures.kaadbackend.contact.controller.response.ContactResponse;
import com.imures.kaadbackend.contact.entity.Contact;
import com.imures.kaadbackend.contact.entity.ContactType;
import com.imures.kaadbackend.contact.mapper.ContactMapper;
import com.imures.kaadbackend.contact.repository.ContactRepository;
import com.imures.kaadbackend.contact.repository.ContactTypeRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ContactService {

    private final ContactRepository contactRepository;
    private final ContactMapper contactMapper;
    private final ContactTypeRepository contactTypeRepository;

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
                                        ()-> new EntityNotFoundException(String.format("Contact with id %s not found", id))
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
    public void createContact(ContactRequest contactRequest) throws BadRequestException {
        if(contactRequest.getEmail() == null && contactRequest.getPhoneNumber() == null){
            throw new BadRequestException("New contact form could not be created if mail or phone is missing");
        }

        String key = generateKey(
                contactRequest.getFullName(),
                Optional.ofNullable(contactRequest.getPhoneNumber()).orElse(""),
                Optional.ofNullable(contactRequest.getEmail()).orElse(""),
                contactRequest.getReason()
        );

        Optional<Contact> result = contactRepository.findByUUID(key);
        if(!isPossibleToCreate(result)){
            return;
        }

        Contact contact = contactMapper.fromRequestToEntity(contactRequest);
        contact.setCreatedAt(OffsetDateTime.now());
        contact.setResolved(false);
        contact.setUUID(key);

        contactRepository.save(contact);
    }

    private boolean isPossibleToCreate(Optional<Contact> result) {
        if(result.isEmpty()){
            return true;
        }
        Duration duration = Duration.between(result.get().getCreatedAt(), OffsetDateTime.now());

        return Math.abs(duration.toMinutes()) > 15;
    }

    public static String generateKey(String fullName, String phoneNumber, String email, String reason) {
        String data = fullName + phoneNumber + email + reason;

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
        Optional.ofNullable(contactRequest.getReason()).ifPresent(contact::setReason);
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
}
