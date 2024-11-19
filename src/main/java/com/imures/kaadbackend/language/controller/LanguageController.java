package com.imures.kaadbackend.language.controller;

import com.imures.kaadbackend.language.controller.request.LanguageRequest;
import com.imures.kaadbackend.language.controller.response.LanguageResponse;
import com.imures.kaadbackend.language.service.LanguageService;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("api/v1/language")
@RequiredArgsConstructor
@CrossOrigin("http://localhost:4200/")
public class LanguageController {

    private final LanguageService languageService;

    @GetMapping
    public ResponseEntity<List<LanguageResponse>> getLanguage() {
        return new ResponseEntity<>(languageService.getAllLanguages(), HttpStatus.OK);
    }

    @GetMapping(path = "{languageId}")
    public ResponseEntity<LanguageResponse> getLanguageById(
            @PathVariable Long languageId
    ) {
        return new ResponseEntity<>(languageService.getLanguageById(languageId), HttpStatus.OK);
    }

    @GetMapping(path = "{languageId}/icon", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<byte[]> getLanguageIcon(
            @PathVariable Long languageId
    ){
        return new ResponseEntity<>(languageService.getLanguageImage(languageId), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<LanguageResponse> createLanguage(
        @RequestPart("body") LanguageRequest languageRequest,
        @RequestPart("image") MultipartFile image
    ) throws IOException {
        return new ResponseEntity<>(languageService.createLanguage(languageRequest, image), HttpStatus.CREATED);
    }

    @PutMapping(path = "{languageId}")
    public ResponseEntity<LanguageResponse> updateLanguage(
            @PathVariable Long languageId,
            @RequestPart("body") LanguageRequest languageRequest,
            @RequestPart("image") @Nullable MultipartFile image
    ) throws IOException {
        return new ResponseEntity<>(languageService.updateLanguage(languageId, languageRequest, image), HttpStatus.OK);
    }
    @CrossOrigin(origins = "http://localhost:4200")
    @DeleteMapping(path = "{languageId}")
    public ResponseEntity<Void> deleteLanguage(
            @PathVariable Long languageId
    ){
        languageService.deleteLanguage(languageId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @RequestMapping(value = "/**", method = RequestMethod.OPTIONS)
    public ResponseEntity<?> handleOptions() {
        return ResponseEntity.ok().build();
    }
}
