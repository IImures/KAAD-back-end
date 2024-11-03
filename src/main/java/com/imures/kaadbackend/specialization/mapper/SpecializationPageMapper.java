package com.imures.kaadbackend.specialization.mapper;

import com.imures.kaadbackend.specialization.controller.response.SpecializationPageResponse;
import com.imures.kaadbackend.specialization.entity.SpecializationPage;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SpecializationPageMapper {

    SpecializationPage fromRequestToEntity(SpecializationPage specializationRequest);

    SpecializationPageResponse fromEntityToResponse(SpecializationPage specialization);

}
