package com.imures.kaadbackend.specialization.service;

import com.imures.kaadbackend.specialization.controller.request.SpecializationRequest;
import com.imures.kaadbackend.specialization.controller.response.SpecializationResponse;
import com.imures.kaadbackend.specialization.entity.Specialization;
import com.imures.kaadbackend.specialization.entity.SpecializationPage;
import com.imures.kaadbackend.specialization.mapper.SpecializationMapper;
import com.imures.kaadbackend.specialization.repository.SpecializationRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SpecializationService {

    private final SpecializationRepository specializationRepository;
    private final SpecializationMapper specializationMapper;

    public List<SpecializationResponse> getAllSpecializations() {
        return specializationRepository.findAll().stream()
                .map(specializationMapper::fromEntityToResponse)
                .toList();
    }

    public SpecializationResponse getSpecialization(Long specId) {
        return specializationMapper.fromEntityToResponse(
                specializationRepository.findById(specId)
                        .orElseThrow(() -> new EntityNotFoundException(String.format("Specialization with id %s not found", specId))
                        )
        );
    }

    public SpecializationResponse createSpecialization(MultipartFile image, SpecializationRequest specializationRequest) throws IOException {
        Specialization specialization = new Specialization();
        specialization.setName(specializationRequest.getName());

        specialization.setImgName(image.getName());
        specialization.setImgType(image.getContentType());
        specialization.setImageData(image.getBytes());

        SpecializationPage page = new SpecializationPage();
        page.setContent(specializationRequest.getContent());

        specialization.setSpecializationPage(page);
        page.setSpecialization(specialization);

        return specializationMapper.fromEntityToResponse(specializationRepository.save(specialization));
    }

    public SpecializationResponse updateSpecialization(MultipartFile image, SpecializationRequest specializationRequest, Long specId) throws IOException {
        Specialization specialization = specializationRepository.findById(specId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Specialization with id %s not found", specId)));

        Optional.ofNullable(specializationRequest.getName()).ifPresent(specialization::setName);
        Optional.ofNullable(specializationRequest.getContent()).ifPresent(s -> specialization.getSpecializationPage().setContent(s));

        if(image != null){
            specialization.setImgName(image.getName());
            specialization.setImgType(image.getContentType());
            specialization.setImageData(image.getBytes());
        }

        return specializationMapper.fromEntityToResponse(specializationRepository.save(specialization));
    }

    public void deleteSpecialization(Long specId) {
        Specialization specialization = specializationRepository.findById(specId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Specialization with id %s not found", specId)));
        specializationRepository.delete(specialization);
    }
}
