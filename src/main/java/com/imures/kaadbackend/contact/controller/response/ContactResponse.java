package com.imures.kaadbackend.contact.controller.response;

import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
public class ContactResponse {
    private Long id;
    private String fullName;
    private String phoneNumber;
    private String email;
    private String reason;
    private OffsetDateTime createdAt;
    private String contactType;

}
