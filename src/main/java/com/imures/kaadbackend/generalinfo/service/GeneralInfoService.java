package com.imures.kaadbackend.generalinfo.service;

import com.imures.kaadbackend.generalinfo.controller.request.GeneralInfoRequest;
import com.imures.kaadbackend.generalinfo.controller.response.GeneralInfoResponse;
import com.imures.kaadbackend.generalinfo.entity.GeneralInfo;
import com.imures.kaadbackend.generalinfo.mapper.GeneralInfoMapper;
import com.imures.kaadbackend.generalinfo.repository.GeneralInfoRepository;
import com.imures.kaadbackend.language.entity.Language;
import com.imures.kaadbackend.language.repository.LanguageRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GeneralInfoService {


    private final GeneralInfoRepository generalInfoRepository;
    private final GeneralInfoMapper generalInfoMapper;
    private final LanguageRepository languageRepository;

    @Transactional(readOnly = true)
    public List<GeneralInfoResponse> getAll() {
        return generalInfoRepository.findAll()
                .stream().map(generalInfoMapper::fromEntityToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public GeneralInfoResponse getById(Long genId) {
        return generalInfoMapper.fromEntityToResponse(
                generalInfoRepository.findById(genId)
                        .orElseThrow(()-> new EntityNotFoundException(String.format("General info with id %s not found", genId)))
        );
    }

    @Transactional //Used in TeamMemberService updateTeamMember!
    public GeneralInfoResponse getByCodeAndLanguage(String code, String languageCode) {
        Optional<GeneralInfo> generalInfoOptional = generalInfoRepository.findByCodeAndLanguage_Code(code, languageCode);

        GeneralInfo generalInfo = generalInfoOptional.orElseGet(() ->
                generalInfoRepository.findByCodeAndLanguage_DefaultLanguage(code, true)
                        .orElseThrow(() -> new EntityNotFoundException(
                                String.format("General info with code: %s not found in any language", code)
                        ))
        );

        return generalInfoMapper.fromEntityToResponse(generalInfo);
    }

    @Transactional
    public GeneralInfo create(GeneralInfoRequest generalInfoRequest) {
        Language language = languageRepository
                .findById(generalInfoRequest.getLanguageId())
                .orElseThrow(()-> new EntityNotFoundException(String.format("Language with id: %s not found", generalInfoRequest.getLanguageId())));
        Optional<GeneralInfo> generalInfo = generalInfoRepository.findByCodeAndLanguage_Code(
                generalInfoRequest.getCode(), language.getCode()
        );
        if (generalInfo.isPresent()) {
            throw new EntityExistsException(String.format("General info with code: %s and language: %s already exists", generalInfoRequest.getCode(), language.getCode()));
        }

        GeneralInfo info = new GeneralInfo();
        info.setContent(generalInfoRequest.getContent());
        info.setCode(generalInfoRequest.getCode());
        info.setLanguage(language);
        Optional.ofNullable(generalInfoRequest.getIsLabel()).ifPresent(info::setIsLabel);
        if(Optional.ofNullable(generalInfoRequest.getIsLabel()).isEmpty()){
            info.setIsLabel(false);
        }

        return generalInfoRepository.save(info);
    }

    @Transactional
    public GeneralInfoResponse update(GeneralInfoRequest generalInfoRequest, String languageCode, String code) {
        Optional<GeneralInfo> optional =  generalInfoRepository.findByCodeAndLanguage_Code(code, languageCode);

        if(optional.isEmpty()) {
            return generalInfoMapper.fromEntityToResponse(
                    create(generalInfoRequest)
            );
        }
        GeneralInfo info = optional.get();

        Optional.ofNullable(generalInfoRequest.getIsLabel()).ifPresent(info::setIsLabel);
        Optional.ofNullable(generalInfoRequest.getContent()).ifPresent(info::setContent);
        Optional.ofNullable(generalInfoRequest.getIsLabel()).ifPresent(info::setIsLabel);
        Optional.ofNullable(generalInfoRequest.getLanguageId()).ifPresent((aLong ->
                info.setLanguage(
                        languageRepository
                            .findById(generalInfoRequest.getLanguageId())
                            .orElseThrow(()-> new EntityNotFoundException(
                                    String.format("Language with id: %s not found", generalInfoRequest.getLanguageId()))
                            )
        )));

        return generalInfoMapper.fromEntityToResponse(generalInfoRepository.save(info));
    }

    @Transactional
    public void delete(Long genId) {
        GeneralInfo info =  generalInfoRepository.findById(genId)
                .orElseThrow(()-> new EntityNotFoundException(String.format("General info with id %s not found", genId)));
        generalInfoRepository.delete(info);
    }

    public List<GeneralInfoResponse> createMany(List<GeneralInfoRequest> generalInfoRequests) {
        List<GeneralInfoResponse> responses = new ArrayList<>();
        for(GeneralInfoRequest generalInfoRequest : generalInfoRequests) {
            Language language = languageRepository
                    .findById(generalInfoRequest.getLanguageId())
                    .orElseThrow(()-> new EntityNotFoundException(String.format("Language with id: %s not found", generalInfoRequest.getLanguageId())));
            Optional<GeneralInfo> generalInfo = generalInfoRepository.findByCodeAndLanguage_Code(
                    generalInfoRequest.getCode(), language.getCode()
            );
            if (generalInfo.isPresent()) {
                throw new EntityExistsException(String.format("General info with code: %s and language: %s already exists", generalInfoRequest.getCode(), language.getCode()));
            }

            GeneralInfo info = new GeneralInfo();
            info.setContent(generalInfoRequest.getContent());
            info.setCode(generalInfoRequest.getCode());
            info.setLanguage(language);
            info.setIsLabel(generalInfoRequest.getIsLabel());

            responses.add(
                    generalInfoMapper.fromEntityToResponse(generalInfoRepository.save(info))
            );
        }
        return responses;
    }

    @Transactional(readOnly = true)
    public List<GeneralInfoResponse> getLabels() {
        return generalInfoRepository.findGeneralInfoByIsLabelAndLanguage_DefaultLanguageTrue(true)
                .stream().map(generalInfoMapper::fromEntityToResponse)
                .sorted(Comparator.comparing(GeneralInfoResponse::getId))
                .toList();
    }
}
