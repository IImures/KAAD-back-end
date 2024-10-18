package com.imures.kaadbackend.teammembder.controller;

import com.imures.kaadbackend.teammembder.controller.request.TeamMemberRequest;
import com.imures.kaadbackend.teammembder.controller.response.TeamMemberResponse;
import com.imures.kaadbackend.teammembder.service.TeamMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("api/v1/team-member")
@RequiredArgsConstructor
@CrossOrigin("http://localhost:4200/")
public class TeamMemberController {

    private final TeamMemberService teamMemberService;

    @GetMapping(path = "{memberId}")
    public ResponseEntity<TeamMemberResponse> getTeamMember(
            @PathVariable("memberId") Long memberId,
            @RequestParam(
                    value = "lang",
                    required = false,
                    defaultValue = "pl"
            ) String languageCode
    ) {
        return new ResponseEntity<>(teamMemberService.getMember(memberId,languageCode), HttpStatus.OK);
    }

    @GetMapping(path = "{memberId}/photo", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<byte[]> getTeamMemberPhoto(
            @PathVariable Long memberId
    ){
        return new ResponseEntity<>(teamMemberService.getMemberPhoto(memberId), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<TeamMemberResponse>> getAllTeamMembers(
            @RequestParam(
                    value = "lang",
                    required = false,
                    defaultValue = "pl"
            ) String languageCode
    ) {
        return new ResponseEntity<>(teamMemberService.getMembers(languageCode), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<TeamMemberResponse> createTeamMember(
           @RequestPart("body") TeamMemberRequest teamMemberRequest,
           @RequestPart("image") MultipartFile image

    ) throws IOException {
        return new ResponseEntity<>(teamMemberService.createMember(teamMemberRequest, image), HttpStatus.CREATED);
    }

    @PutMapping(path = "{memberId}")
    public ResponseEntity<TeamMemberResponse> updateTeamMember(
            @RequestPart("body") TeamMemberRequest teamMemberRequest,
            @RequestPart("image") @Nullable MultipartFile image,
            @PathVariable Long memberId

    ) throws IOException {
        return new ResponseEntity<>(teamMemberService.updateMember(teamMemberRequest, image, memberId), HttpStatus.OK);
    }

    @DeleteMapping(path = "{memberId}")
    public ResponseEntity<Void> deleteTeamMember(
            @PathVariable Long memberId
    ){
        teamMemberService.deleteMember(memberId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
