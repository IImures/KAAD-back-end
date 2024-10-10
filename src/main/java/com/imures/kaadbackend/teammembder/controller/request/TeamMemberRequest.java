package com.imures.kaadbackend.teammembder.controller.request;

import lombok.Data;

@Data
public class TeamMemberRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private Long priority;
}
