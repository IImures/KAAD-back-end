package com.imures.kaadbackend.specialization.controller.request;

import com.imures.kaadbackend.generalinfo.controller.request.GeneralInfoRequest;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SpecializationPageRequest {
    private List<GeneralInfoRequest> pageContents;
}