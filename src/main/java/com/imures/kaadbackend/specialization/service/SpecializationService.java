package com.imures.kaadbackend.specialization.service;

import com.imures.kaadbackend.generalinfo.controller.request.GeneralInfoRequest;
import com.imures.kaadbackend.generalinfo.controller.response.GeneralInfoResponse;
import com.imures.kaadbackend.generalinfo.entity.GeneralInfo;
import com.imures.kaadbackend.generalinfo.mapper.GeneralInfoMapperImpl;
import com.imures.kaadbackend.generalinfo.service.GeneralInfoService;
import com.imures.kaadbackend.language.entity.Language;
import com.imures.kaadbackend.language.repository.LanguageRepository;
import com.imures.kaadbackend.specialization.controller.request.SpecializationPageRequest;
import com.imures.kaadbackend.specialization.controller.request.SpecializationRequest;
import com.imures.kaadbackend.specialization.controller.response.SpecializationPageResponse;
import com.imures.kaadbackend.specialization.controller.response.SpecializationResponse;
import com.imures.kaadbackend.specialization.entity.Specialization;
import com.imures.kaadbackend.specialization.entity.SpecializationPage;
import com.imures.kaadbackend.specialization.mapper.SpecializationMapper;
import com.imures.kaadbackend.specialization.mapper.SpecializationPageMapper;
import com.imures.kaadbackend.specialization.repository.SpecializationPageRepository;
import com.imures.kaadbackend.specialization.repository.SpecializationRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SpecializationService {

    private final SpecializationRepository specializationRepository;
    private final SpecializationMapper specializationMapper;
    private final GeneralInfoService generalInfoService;
    private final GeneralInfoMapperImpl generalInfoMapper;
    private final SpecializationPageRepository specializationPageRepository;
    private final SpecializationPageMapper specializationPageMapper;
    private final LanguageRepository languageRepository;

    @Transactional(readOnly = true)
    public List<SpecializationResponse> getAllSpecializations(String languageCode, Boolean showHidden) {

        List<Specialization> specializations = specializationRepository.findSpecializations(showHidden);
        List<SpecializationResponse> specializationResponses = new ArrayList<>();

        for (Specialization specialization : specializations) {
            SpecializationResponse specializationResponse = new SpecializationResponse();
            specializationResponse.setId(specialization.getId());
            specializationResponse.setGeneralInfo(
                    generalInfoMapper.fromEntityToResponse(
                            getSpecializationGeneralInfo(specialization, languageCode)
                    )
            );

            specializationResponses.add(specializationResponse);
        }

        return specializationResponses;
    }

    @Transactional(readOnly = true)
    public SpecializationResponse getSpecialization(Long specId, String languageCode, Boolean showHidden) {
        Specialization specialization =specializationRepository.findById(specId)
                        .orElseThrow(() -> new EntityNotFoundException(String.format("Specialization with id %s not found", specId)));

        if(specialization.isHidden() && !showHidden){
            throw new EntityNotFoundException(String.format("Specialization with id %s not found", specId));
        }

        SpecializationResponse specializationResponse = specializationMapper.fromEntityToResponse(specialization);
        GeneralInfo generalInfo = getSpecializationGeneralInfo(specialization, languageCode);

        GeneralInfoResponse generalInfoResponse = generalInfoMapper.fromEntityToResponse(generalInfo);
        specializationResponse.setGeneralInfo(generalInfoResponse);

        return specializationResponse;
    }

    @Transactional(readOnly = true)
    protected GeneralInfo getSpecializationGeneralInfo(Specialization specialization, String languageCode) {
        return specialization.getSpecNames().stream()
                .filter(generalInfo -> generalInfo.getLanguage().getCode().equals(languageCode))
                .findFirst()
                .orElseGet(() -> specialization.getSpecNames().stream()
                        .filter( generalInfo -> generalInfo.getLanguage().getDefaultLanguage())
                        .findFirst().orElseGet(()-> specialization.getSpecNames().get(0))
                );
    }

    @Transactional
    public SpecializationResponse createSpecialization(MultipartFile image, SpecializationRequest specializationRequest) throws IOException {
        if(!specializationRequest.getIsHidden() && image == null){
            throw new BadRequestException("Specialization image is mandatory");
        }


        Specialization specialization = new Specialization();

        if (!specializationRequest.getIsHidden()) {
            specialization.setImgName(image.getName());
            specialization.setImageData(image.getBytes());
        }
        specialization.setHidden(specializationRequest.getIsHidden());

        specialization = specializationRepository.save(specialization);

        specialization.setSpecNames(new ArrayList<>());
        for(GeneralInfoRequest generalInfoRequest : specializationRequest.getSpecializationNames()){
            generalInfoRequest.setCode("s_"+generalInfoRequest.getCode() + specialization.getId());

            GeneralInfo generalInfo = generalInfoService.create(generalInfoRequest);
            specialization.addGeneralInfo(generalInfo);
        }

        specializationRepository.save(specialization);

        return specializationMapper.fromEntityToResponse(specializationRepository.save(specialization));
    }

    @Transactional
    public SpecializationResponse updateSpecialization(MultipartFile image, SpecializationRequest specializationRequest, Long specId) throws IOException {
        Specialization specialization = specializationRepository.findById(specId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Specialization with id %s not found", specId)));

        if(image != null){
            specialization.setImgName(image.getName());
            specialization.setImageData(image.getBytes());
        }
        Optional.ofNullable(specializationRequest.getIsHidden()).ifPresent(specialization::setHidden);

        for (GeneralInfoRequest generalInfoRequest : specializationRequest.getSpecializationNames()) {

            Language language = languageRepository.findById(generalInfoRequest.getLanguageId())
                    .orElseThrow(()-> new EntityNotFoundException(String.format("Language with id %s not found", generalInfoRequest.getLanguageId())));

            Optional<GeneralInfo> existingGeneralInfo = specialization.getSpecNames().stream()
                    .filter(generalInfo -> generalInfo.getCode().equals(getSpecCode(generalInfoRequest, specId)))
                    .filter(generalInfo -> generalInfo.getLanguage().getCode().equals(language.getCode()))
                    .findFirst();

            existingGeneralInfo.ifPresent(generalInfo -> generalInfoService.update(generalInfoRequest, generalInfo.getId()));

            if(existingGeneralInfo.isEmpty()){
                generalInfoRequest.setCode(getSpecCode(generalInfoRequest, specialization.getId()));

                GeneralInfo generalInfo = generalInfoService.create(generalInfoRequest);
                specialization.addGeneralInfo(generalInfo);
            }
        }

        return specializationMapper.fromEntityToResponse(specializationRepository.save(specialization));
    }

    public void deleteSpecialization(Long specId) {
        Specialization specialization = specializationRepository.findById(specId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Specialization with id %s not found", specId)));

        specializationRepository.delete(specialization);
    }

    public byte[] getSpecializationPhoto(Long specId) {
        return specializationRepository.findById(specId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Specialization Photo with id: %s, not found", specId)))
                .getImageData();
    }

    @Transactional(readOnly = true)
    public SpecializationPageResponse getSpecializationPage(Long specId, String languageCode) {
        SpecializationPage specializationPage = specializationPageRepository.findById(specId)
                .orElseThrow(()-> new EntityNotFoundException(String.format("Specialization Page with id: %s, not found", specId)));

        if(
                specializationPage
                        .getSpecialization()
                        .isHidden()
        ){
            throw new EntityNotFoundException(String.format("Specialization Page with id: %s, not found", specId));
        }
        SpecializationPageResponse specializationPageResponse = specializationPageMapper.fromEntityToResponse(specializationPage);
        GeneralInfo generalInfo = getSpecializationPageGeneralInfo(specializationPage, languageCode);

        GeneralInfoResponse generalInfoResponse = generalInfoMapper.fromEntityToResponse(generalInfo);
        specializationPageResponse.setGeneralInfo(generalInfoResponse);

        return specializationPageResponse;
    }

    @Transactional(readOnly = true)
    public byte[] getSpecializationPagePhoto(Long specId) {
        return specializationPageRepository.findById(specId)
                .orElseThrow(()-> new EntityNotFoundException(String.format("Specialization Page with id: %s, not found", specId)))
                .getImageData();
    }


    @Transactional(readOnly = true)
    protected GeneralInfo getSpecializationPageGeneralInfo(SpecializationPage specializationPage, String languageCode) {
        return specializationPage.getContent().stream()
                .filter(generalInfo -> generalInfo.getLanguage().getCode().equals(languageCode))
                .findFirst()
                .orElseGet(() -> specializationPage.getContent().stream()
                        .filter( generalInfo -> generalInfo.getLanguage().getDefaultLanguage())
                        .findFirst().orElseGet(()-> specializationPage.getContent().get(0))
                );
    }

    @Transactional
    public SpecializationPageResponse createSpecializationPage(SpecializationPageRequest specializationPageRequest, MultipartFile image, Long specId) throws IOException {
        Specialization specialization = specializationRepository.findById(specId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Specialization with id %s not found", specId)));

        if(specialization.isHidden()) throw new EntityNotFoundException(String.format("Specialization with id %s not found", specId));
        if(specialization.getSpecializationPage() != null) throw new EntityExistsException("Specialization page already exists");

        SpecializationPage specializationPage = new SpecializationPage();

        specializationPage.setImageData(image != null ? image.getBytes() : null);
        specializationPage.setImageName(image != null ? image.getName() : null);

        specializationPage.setContent(new ArrayList<>());
        specializationPage.setSpecialization(specialization);

        specializationPage = specializationPageRepository.save(specializationPage);

        for(GeneralInfoRequest generalInfoRequest : specializationPageRequest.getPageContents()){
            generalInfoRequest.setCode(getSpecPageCode(generalInfoRequest, specializationPage.getId()));

            GeneralInfo generalInfo = generalInfoService.create(generalInfoRequest);
            specializationPage.addGeneralInfo(generalInfo);
        }

        specializationPage = specializationPageRepository.save(specializationPage);

        return specializationPageMapper.fromEntityToResponse(specializationPage);
    }

    @Transactional
    public SpecializationPageResponse updateSpecializationPage(SpecializationPageRequest specializationPageRequest, MultipartFile image, Long specId) throws IOException {
        SpecializationPage specializationPage = specializationPageRepository.findById(specId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Specialization Page with id %s not found", specId)));

        if (image != null) {
            specializationPage.setImageData(image.getBytes());
            specializationPage.setImageName(image.getName());
        }

        Long specPageId = specializationPage.getId();
        if(specializationPageRequest == null){
            specializationPage = specializationPageRepository.save(specializationPage);
            return specializationPageMapper.fromEntityToResponse(specializationPage);
        }

        for (GeneralInfoRequest generalInfoRequest : specializationPageRequest.getPageContents()) {

            Language language = languageRepository.findById(generalInfoRequest.getLanguageId())
                    .orElseThrow(()-> new EntityNotFoundException(String.format("Language with id %s not found", generalInfoRequest.getLanguageId())));
            Optional<GeneralInfo> existingGeneralInfo = specializationPage.getContent().stream()
                    .filter(generalInfo -> generalInfo.getCode().equals(getSpecPageCode(generalInfoRequest, specPageId)))
                    .filter(generalInfo -> generalInfo.getLanguage().getCode().equals(language.getCode()))
                    .findFirst();

            existingGeneralInfo.ifPresent(generalInfo -> generalInfoService.update(generalInfoRequest, existingGeneralInfo.get().getId()));
            if(existingGeneralInfo.isEmpty()){
                generalInfoRequest.setCode(getSpecPageCode(generalInfoRequest, specializationPage.getId()));

                GeneralInfo generalInfo = generalInfoService.create(generalInfoRequest);
                specializationPage.addGeneralInfo(generalInfo);
            }
        }

        specializationPage = specializationPageRepository.save(specializationPage);

        return specializationPageMapper.fromEntityToResponse(specializationPage);
    }


    private static String getSpecPageCode(GeneralInfoRequest generalInfoRequest, Long specializationPageId) {
        return "sp_" + generalInfoRequest.getCode() + specializationPageId;
    }

    private static String getSpecCode(GeneralInfoRequest generalInfoRequest, Long specializationId) {
        return "s_" + generalInfoRequest.getCode() + specializationId;
    }
}
