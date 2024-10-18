package com.imures.kaadbackend.specialization.service;

import com.imures.kaadbackend.generalinfo.controller.request.GeneralInfoRequest;
import com.imures.kaadbackend.generalinfo.controller.response.GeneralInfoResponse;
import com.imures.kaadbackend.generalinfo.entity.GeneralInfo;
import com.imures.kaadbackend.generalinfo.mapper.GeneralInfoMapperImpl;
import com.imures.kaadbackend.generalinfo.service.GeneralInfoService;
import com.imures.kaadbackend.specialization.controller.request.SpecializationRequest;
import com.imures.kaadbackend.specialization.controller.response.SpecializationResponse;
import com.imures.kaadbackend.specialization.entity.Specialization;
import com.imures.kaadbackend.specialization.mapper.SpecializationMapper;
import com.imures.kaadbackend.specialization.repository.SpecializationRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SpecializationService {

    private final SpecializationRepository specializationRepository;
    private final SpecializationMapper specializationMapper;
    private final GeneralInfoService generalInfoService;
    private final GeneralInfoMapperImpl generalInfoMapper;

    @Transactional(readOnly = true)
    public List<SpecializationResponse> getAllSpecializations(String languageCode) {

        List<Specialization> specializations = specializationRepository.findAll();
        List<SpecializationResponse> specializationResponses = new ArrayList<>();

        for (Specialization specialization : specializations) {
            SpecializationResponse specializationResponse = new SpecializationResponse();
            specializationResponse.setId(specialization.getId());
            specializationResponse.setGeneralInfo(
                    generalInfoMapper.fromEntityToResponse(
                            specialization.getDetails().stream()
                            .filter(generalInfo -> generalInfo.getLanguage().getCode().equals(languageCode))
                            .findFirst()
                            .orElseGet(() -> specialization.getDetails().stream()
                                    .filter( generalInfo -> generalInfo.getLanguage().getDefaultLanguage())
                                    .findFirst().orElseGet(()-> specialization.getDetails().get(0))
                            ))
            );

            specializationResponses.add(specializationResponse);
        }

        return specializationResponses;
    }

    @Transactional(readOnly = true)
    public SpecializationResponse getSpecialization(Long specId, String languageCode) {
        Specialization specialization =specializationRepository.findById(specId)
                        .orElseThrow(() -> new EntityNotFoundException(String.format("Specialization with id %s not found", specId)));

        SpecializationResponse specializationResponse = specializationMapper.fromEntityToResponse(specialization);
        GeneralInfo generalInfo = getSpecializationGeneralInfo(specialization, languageCode);

        GeneralInfoResponse generalInfoResponse = generalInfoMapper.fromEntityToResponse(generalInfo);
        specializationResponse.setGeneralInfo(generalInfoResponse);

        return specializationResponse;
    }

    @Transactional(readOnly = true)
    protected GeneralInfo getSpecializationGeneralInfo(Specialization specialization, String languageCode) {
        return specialization.getDetails().stream()
                .filter(generalInfo -> generalInfo.getLanguage().getCode().equals(languageCode))
                .findFirst()
                .orElseGet(() -> specialization.getDetails().stream()
                        .filter( generalInfo -> generalInfo.getLanguage().getDefaultLanguage())
                        .findFirst().orElseGet(()-> specialization.getDetails().get(0))
                );
    }

    @Transactional
    public SpecializationResponse createSpecialization(MultipartFile image, SpecializationRequest specializationRequest) throws IOException {
        Specialization specialization = new Specialization();

        specialization.setImgName(image.getName());
        specialization.setImageData(image.getBytes());

        specialization = specializationRepository.save(specialization);

        specialization.setDetails(new ArrayList<>());
        for(GeneralInfoRequest generalInfoRequest : specializationRequest.getGeneralInfos()){
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

//        Optional.ofNullable(specializationRequest.getName()).ifPresent(specialization::setName);
//        Optional.ofNullable(specializationRequest.getContent()).ifPresent(s -> specialization.getSpecializationPage().setContent(s));

        if(image != null){
            specialization.setImgName(image.getName());
            specialization.setImageData(image.getBytes());
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
}
