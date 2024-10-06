package com.imures.kaadbackend.language.mapper;

import com.imures.kaadbackend.language.controller.response.LanguageResponse;
import com.imures.kaadbackend.language.entity.Language;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LanguageMapper {

    LanguageResponse fromEntityToResponse(Language language);

}
