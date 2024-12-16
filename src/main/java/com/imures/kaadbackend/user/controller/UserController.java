package com.imures.kaadbackend.user.controller;

import com.imures.kaadbackend.configuration.JwtService;
import com.imures.kaadbackend.user.controller.request.AuthenticationRequest;
import com.imures.kaadbackend.user.controller.request.UserRequest;
import com.imures.kaadbackend.user.controller.response.AuthenticationResponse;
import com.imures.kaadbackend.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/user")
public class UserController {

    private final UserService userService;
    private final JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> createUser(
            @RequestBody UserRequest request
    ){
        return new ResponseEntity<>(userService.createUser(request), HttpStatus.CREATED);
    }

    @PutMapping("/blog-image")
    public ResponseEntity<Void> changeBlogImage(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String jwtToken,
            @RequestPart("image") MultipartFile image
    ) throws IOException {
        String username = jwtService.extractUsername(jwtToken.substring(7));
        userService.updateBlogImage(image, username);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(value = "/login"   )
    public ResponseEntity<AuthenticationResponse> login(
            @RequestBody AuthenticationRequest request
    ){
        return new ResponseEntity<>(userService.authenticate(request), HttpStatus.OK);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthenticationResponse> refresh(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String refreshToken
    ){
        return new ResponseEntity<>(userService.refresh(refreshToken.substring(7)), HttpStatus.OK);
    }

    @PostMapping("/valid")
    public ResponseEntity<Void> valid(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token
    ){
        userService.validate(token.substring(7));
        return new ResponseEntity<>(HttpStatus.OK);
    }

}