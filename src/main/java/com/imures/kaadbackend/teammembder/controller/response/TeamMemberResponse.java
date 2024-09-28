package com.imures.kaadbackend.teammembder.controller.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TeamMemberResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String imageData;
}