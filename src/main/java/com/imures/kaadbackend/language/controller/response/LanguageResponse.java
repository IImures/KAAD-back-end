package com.imures.kaadbackend.language.controller.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LanguageResponse {

    private Long id;
    private String language;
    private String code;
    private Boolean defaultLanguage;
}

