package com.imures.kaadbackend.contact.controller.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ContactRequest {
    private String fullName;
    private String phoneNumber;
    private String email;
    private Long contactTypeId;
    private Long specializationId;
    private String languageCode;
}
