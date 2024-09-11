package com.imures.kaadbackend.user.controller.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRequest {
    private String password;
    private String email;
    private String firstName;
    private String lastName;
    private Long[] roles;

}