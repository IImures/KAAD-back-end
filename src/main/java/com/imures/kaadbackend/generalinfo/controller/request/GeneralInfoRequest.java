package com.imures.kaadbackend.generalinfo.controller.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GeneralInfoRequest {
    private String content;
    private String code;
    private Long languageId;
    private Boolean isLabel = null;
}
