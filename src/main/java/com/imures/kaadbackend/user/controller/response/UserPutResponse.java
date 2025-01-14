package com.imures.kaadbackend.user.controller.response;

import lombok.Data;

@Data
public class UserPutResponse {
    private String email;
    private String firstName;
    private String lastName;
}
