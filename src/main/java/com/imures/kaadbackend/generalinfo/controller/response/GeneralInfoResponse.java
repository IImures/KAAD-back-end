package com.imures.kaadbackend.generalinfo.controller.response;

import com.imures.kaadbackend.language.controller.response.LanguageResponse;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GeneralInfoResponse {
    private Long id;
    private String content;
    private String code;
    private LanguageResponse language;
    private Boolean isLabel;
}
