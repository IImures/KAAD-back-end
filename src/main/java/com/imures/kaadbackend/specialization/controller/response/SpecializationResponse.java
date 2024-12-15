package com.imures.kaadbackend.specialization.controller.response;

import com.imures.kaadbackend.generalinfo.controller.response.GeneralInfoResponse;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SpecializationResponse {
    private Long id;
    private GeneralInfoResponse generalInfo;
    private Boolean isHidden;
}
