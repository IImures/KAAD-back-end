package com.imures.kaadbackend.specialization.controller.response;

import com.imures.kaadbackend.generalinfo.controller.response.GeneralInfoResponse;
import lombok.Data;

@Data
public class SpecializationPageResponse {
    private Long id;
    private GeneralInfoResponse generalInfo;
}
