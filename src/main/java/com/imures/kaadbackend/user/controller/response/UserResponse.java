package com.imures.kaadbackend.user.controller.response;

import com.imures.kaadbackend.roles.response.RoleResponse;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserResponse {
    private String email;
    private RoleResponse[] roles;

}