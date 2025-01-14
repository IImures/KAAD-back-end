package com.imures.kaadbackend.specialization.controller;

import com.imures.kaadbackend.specialization.controller.request.SpecializationPageRequest;
import com.imures.kaadbackend.specialization.controller.request.SpecializationRequest;
import com.imures.kaadbackend.specialization.controller.response.SpecializationPageResponse;
import com.imures.kaadbackend.specialization.controller.response.SpecializationResponse;
import com.imures.kaadbackend.specialization.service.SpecializationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.security.access.prepost.PreAuthorize;
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
    public ResponseEntity<List<SpecializationResponse>> getAllSpecializations(
            @RequestParam(
                    value = "lang",
                    required = false,
                    defaultValue = "pl"
            ) String languageCode,
            @RequestParam(
                    value = "showHidden",
                    required = false,
                    defaultValue = "false"
            ) Boolean showHidden
    ) {
     return new ResponseEntity<>(specializationService.getAllSpecializations(languageCode, showHidden), HttpStatus.OK);
    }

    @GetMapping(path = "{specId}")
    public ResponseEntity<SpecializationResponse> getSpecialization(
            @PathVariable Long specId,
            @RequestParam(
                    value = "lang",
                    required = false,
                    defaultValue = "pl"
            ) String languageCode,
            @RequestParam(
                    value = "showHidden",
                    required = false,
                    defaultValue = "false"
            ) Boolean showHidden
    ) {
        return new ResponseEntity<>(specializationService.getSpecialization(specId, languageCode, showHidden), HttpStatus.OK);
    }

    @GetMapping(path = {"{specId}/photo"}, produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<byte[]> getSpecializationPhoto(
            @PathVariable Long specId
    ){
        return new ResponseEntity<>(specializationService.getSpecializationPhoto(specId), HttpStatus.OK);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping
    public ResponseEntity<SpecializationResponse> createSpecialization(
            @Nullable @RequestPart("image") MultipartFile image,
            @RequestPart("body") SpecializationRequest specializationRequest
    ) throws IOException {
        return new ResponseEntity<>(specializationService.createSpecialization(image, specializationRequest), HttpStatus.CREATED);
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping(path = "{specId}")
    public ResponseEntity<SpecializationResponse> updateSpecialization(
            @RequestPart("image") @Nullable MultipartFile image,
            @RequestPart("body") SpecializationRequest specializationRequest,
            @PathVariable Long specId
    ) throws IOException {
        return new ResponseEntity<>(specializationService.updateSpecialization(image, specializationRequest, specId), HttpStatus.OK);
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping(path = "{specId}")
    public ResponseEntity<Void> deleteSpecialization(
            @PathVariable Long specId
    ){
        specializationService.deleteSpecialization(specId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


    @GetMapping(path = {"{specId}/page"})
    public ResponseEntity<SpecializationPageResponse> getSpecializationPage(
            @PathVariable Long specId,
            @RequestParam(
                    value = "lang",
                    required = false,
                    defaultValue = "pl"
            ) String languageCode
    ){
        return new ResponseEntity<>(specializationService.getSpecializationPage(specId, languageCode), HttpStatus.OK);
    }

    @GetMapping(path = {"{specId}/page/photo"}, produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<byte[]> getSpecializationPagePhoto(
            @PathVariable Long specId
    ){
        return new ResponseEntity<>(specializationService.getSpecializationPagePhoto(specId), HttpStatus.OK);
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping(path = "{specId}/page/photo")
    public ResponseEntity<Void> deleteSpecializationPagePhoto(
            @PathVariable Long specId
    ){
        specializationService.deleteSpecializationPagePhoto(specId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping(path = {"{specId}/page"})
    public ResponseEntity<SpecializationPageResponse> createSpecializationPage(
            @PathVariable Long specId,
            @RequestPart("image") @Nullable MultipartFile image,
            @RequestPart("body") SpecializationPageRequest specializationPageRequest
    ) throws IOException {
        return new ResponseEntity<>(specializationService.createSpecializationPage(specializationPageRequest, image, specId), HttpStatus.CREATED);
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping(path = {"{specId}/page"})
    public ResponseEntity<SpecializationPageResponse> updateSpecializationPage(
            @PathVariable Long specId,
            @RequestPart("image") @Nullable MultipartFile image,
            @RequestPart("body") @Nullable SpecializationPageRequest specializationPageRequest
    ) throws IOException {
        return new ResponseEntity<>(specializationService.updateSpecializationPage(specializationPageRequest, image, specId), HttpStatus.CREATED);
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping(path = {"{specId}/page"})
    public ResponseEntity<Void> deleteSpecializationPage(
            @PathVariable Long specId,
            @RequestParam(
                    value = "lang",
                    required = false
            ) String languageCode
    ){
        if(languageCode == null){
            specializationService.deleteSpecializationPage(specId);
        }else{
            specializationService.deleteSpecializationPageOfLanguage(specId, languageCode);
        }

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


}
