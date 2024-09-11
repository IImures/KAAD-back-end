package com.imures.kaadbackend.generalinfo.controller;

import com.imures.kaadbackend.generalinfo.controller.request.GeneralInfoRequest;
import com.imures.kaadbackend.generalinfo.controller.response.GeneralInfoResponse;
import com.imures.kaadbackend.generalinfo.service.GeneralInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/gen-inf")
@RequiredArgsConstructor
public class GeneralInfoController {

    private final GeneralInfoService generalInfoService;

    @GetMapping
    public ResponseEntity<List<GeneralInfoResponse>> getAll(){
        return new ResponseEntity<>(generalInfoService.getAll(), HttpStatus.OK);
    }

    @GetMapping("{genId}")
    public ResponseEntity<GeneralInfoResponse> getById(
            @PathVariable Long genId
    ){
        return new ResponseEntity<>(generalInfoService.getById(genId), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<GeneralInfoResponse> createGeneralInfo(
            @RequestBody GeneralInfoRequest generalInfoRequest
    ){
        return new ResponseEntity<>(generalInfoService.create(generalInfoRequest), HttpStatus.CREATED);
    }

    @PutMapping("{genId}")
    public ResponseEntity<GeneralInfoResponse> createGeneralInfo(
            @RequestBody GeneralInfoRequest generalInfoRequest,
            @PathVariable Long genId
    ){
        return new ResponseEntity<>(generalInfoService.update(generalInfoRequest, genId), HttpStatus.CREATED);
    }

    @DeleteMapping("{genId}")
    public ResponseEntity<Void> deleteGeneralInfo(
            @PathVariable Long genId
    ){
        generalInfoService.delete(genId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
