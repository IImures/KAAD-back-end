package com.imures.kaadbackend.generalinfo.service;

import com.imures.kaadbackend.generalinfo.controller.request.GeneralInfoRequest;
import com.imures.kaadbackend.generalinfo.controller.response.GeneralInfoResponse;
import com.imures.kaadbackend.generalinfo.entity.GeneralInfo;
import com.imures.kaadbackend.generalinfo.mapper.GeneralInfoMapper;
import com.imures.kaadbackend.generalinfo.repository.GeneralInfoRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GeneralInfoService {


    private final GeneralInfoRepository generalInfoRepository;
    private final GeneralInfoMapper generalInfoMapper;

    public List<GeneralInfoResponse> getAll() {
        return generalInfoRepository.findAll()
                .stream().map(generalInfoMapper::fromEntityToResponse)
                .toList();
    }

    public GeneralInfoResponse getById(Long genId) {
        return generalInfoMapper.fromEntityToResponse(
                generalInfoRepository.findById(genId)
                        .orElseThrow(()-> new EntityNotFoundException(String.format("General info with id %s not found", genId)))
        );
    }

    public GeneralInfoResponse create(GeneralInfoRequest generalInfoRequest) {
        GeneralInfo info = new GeneralInfo();
        info.setContent(generalInfoRequest.getContent());
        return generalInfoMapper.fromEntityToResponse(generalInfoRepository.save(info));
    }

    public GeneralInfoResponse update(GeneralInfoRequest generalInfoRequest, Long genId) {
        GeneralInfo info =  generalInfoRepository.findById(genId)
                .orElseThrow(()-> new EntityNotFoundException(String.format("General info with id %s not found", genId)));

        Optional.ofNullable(generalInfoRequest.getContent()).ifPresent(info::setContent);

        return generalInfoMapper.fromEntityToResponse(generalInfoRepository.save(info));
    }

    public void delete(Long genId) {
        GeneralInfo info =  generalInfoRepository.findById(genId)
                .orElseThrow(()-> new EntityNotFoundException(String.format("General info with id %s not found", genId)));
        generalInfoRepository.delete(info);
    }
}
