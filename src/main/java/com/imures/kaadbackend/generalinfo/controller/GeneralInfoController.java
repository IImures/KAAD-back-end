package com.imures.kaadbackend.generalinfo.controller;

import com.imures.kaadbackend.generalinfo.controller.request.GeneralInfoRequest;
import com.imures.kaadbackend.generalinfo.controller.response.GeneralInfoResponse;
import com.imures.kaadbackend.generalinfo.mapper.GeneralInfoMapperImpl;
import com.imures.kaadbackend.generalinfo.service.GeneralInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/gen-inf")
@RequiredArgsConstructor
@CrossOrigin("http://localhost:4200/")
public class GeneralInfoController {

    private final GeneralInfoService generalInfoService;
    private final GeneralInfoMapperImpl generalInfoMapper;

    @GetMapping
    public ResponseEntity<List<GeneralInfoResponse>> getAll(){
        return new ResponseEntity<>(generalInfoService.getAll(), HttpStatus.OK);
    }

//    @GetMapping("{genId}")
//    public ResponseEntity<GeneralInfoResponse> getById(
//            @PathVariable Long genId
//    ){
//        return new ResponseEntity<>(generalInfoService.getById(genId), HttpStatus.OK);
//    }
    @GetMapping("{generalCode}")
    public ResponseEntity<GeneralInfoResponse> getByCodeAndLanguage(
            @PathVariable String generalCode,
            @RequestParam(
                    value = "lang",
                    required = false,
                    defaultValue = "pl"
            ) String languageCode

    ){
        return new ResponseEntity<>(generalInfoService.getByCodeAndLanguage(generalCode, languageCode), HttpStatus.OK);
    }

    @GetMapping(path = "labels")
    public ResponseEntity<List<GeneralInfoResponse>> getAllLabels(){
        return new ResponseEntity<>(generalInfoService.getLabels(), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<GeneralInfoResponse> createGeneralInfo(
            @RequestBody GeneralInfoRequest generalInfoRequest
    ){
        return new ResponseEntity<>(
                generalInfoMapper.fromEntityToResponse(
                    generalInfoService.create(generalInfoRequest)
                ),
                HttpStatus.CREATED
        );
    }

    @PostMapping(path ="many")
    public ResponseEntity<List<GeneralInfoResponse>> createManyGeneralInfos(
            @RequestBody List<GeneralInfoRequest> generalInfoRequest
    ){
        return new ResponseEntity<>(
                generalInfoService.createMany(generalInfoRequest),
                HttpStatus.CREATED
        );
    }


    @PutMapping
    public ResponseEntity<GeneralInfoResponse> updateGeneralInfo(
            @RequestBody GeneralInfoRequest generalInfoRequest,
            @RequestParam(
                    value = "lang"
            ) String languageCode,
            @RequestParam(
                    value = "code"
            ) String code
    ){
        return new ResponseEntity<>(generalInfoService.update(generalInfoRequest, languageCode, code), HttpStatus.OK);
    }

    @DeleteMapping("{genId}")
    public ResponseEntity<Void> deleteGeneralInfo(
            @PathVariable Long genId
    ){
        generalInfoService.delete(genId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
