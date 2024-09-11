package com.imures.kaadbackend.specialization.controller;

import com.imures.kaadbackend.specialization.controller.request.SpecializationRequest;
import com.imures.kaadbackend.specialization.controller.response.SpecializationResponse;
import com.imures.kaadbackend.specialization.service.SpecializationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("api/v1/specialization")
@RequiredArgsConstructor
public class SpecializationController {

    private final SpecializationService specializationService;

    @GetMapping
    public ResponseEntity<List<SpecializationResponse>> getAllSpecializations() {
     return new ResponseEntity<>(specializationService.getAllSpecializations(), HttpStatus.OK);
    }

    @GetMapping(path = "{specId}")
    public ResponseEntity<SpecializationResponse> getSpecialization(
            @PathVariable Long specId
    ) {
        return new ResponseEntity<>(specializationService.getSpecialization(specId), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<SpecializationResponse> createSpecialization(
            @RequestPart("image") MultipartFile image,
            @RequestPart("data") SpecializationRequest specializationRequest
    ) throws IOException {
        return new ResponseEntity<>(specializationService.createSpecialization(image, specializationRequest), HttpStatus.CREATED);
    }

    @PutMapping(path = "{specId}")
    public ResponseEntity<SpecializationResponse> updateSpecialization(
            @RequestPart("image") @Nullable MultipartFile image,
            @RequestPart("data") SpecializationRequest specializationRequest,
            @PathVariable Long specId
    ) throws IOException {
        return new ResponseEntity<>(specializationService.updateSpecialization(image, specializationRequest, specId), HttpStatus.OK);
    }

    @DeleteMapping(path = "{specId}")
    public ResponseEntity<Void> deleteSpecialization(
            @PathVariable Long specId
    ){
        specializationService.deleteSpecialization(specId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
