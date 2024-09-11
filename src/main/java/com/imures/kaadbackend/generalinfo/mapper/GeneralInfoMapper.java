package com.imures.kaadbackend.generalinfo.mapper;

import com.imures.kaadbackend.generalinfo.controller.response.GeneralInfoResponse;
import com.imures.kaadbackend.generalinfo.entity.GeneralInfo;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface GeneralInfoMapper {

    GeneralInfoResponse fromEntityToResponse(GeneralInfo generalInfo);

}
