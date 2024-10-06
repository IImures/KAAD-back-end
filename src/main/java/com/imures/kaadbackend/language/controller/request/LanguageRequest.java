package com.imures.kaadbackend.language.controller.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LanguageRequest {
    private String language;
    private String code;
    private Boolean defaultLanguage;
}
