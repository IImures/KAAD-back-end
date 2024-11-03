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
