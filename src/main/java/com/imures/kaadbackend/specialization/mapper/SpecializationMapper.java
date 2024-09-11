package com.imures.kaadbackend.specialization.mapper;

import com.imures.kaadbackend.specialization.controller.request.SpecializationRequest;
import com.imures.kaadbackend.specialization.controller.response.SpecializationResponse;
import com.imures.kaadbackend.specialization.entity.Specialization;
import org.mapstruct.Mapper;

import java.util.Base64;

@Mapper(componentModel = "spring")
public interface SpecializationMapper {

    Specialization fromRequestToEntity(SpecializationRequest specializationRequest);

    SpecializationResponse fromEntityToResponse(Specialization specialization);

    default String map(byte[] imageData) {
        return imageData != null ? "data:image/png;base64," + Base64.getEncoder().encodeToString(imageData) : null;
    }
}

