package com.imures.kaadbackend.teammembder.controller.request;

import com.imures.kaadbackend.generalinfo.controller.request.GeneralInfoRequest;
import lombok.Data;

@Data
public class TeamMemberRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private GeneralInfoRequest[] description;
    private Long priority;
}
