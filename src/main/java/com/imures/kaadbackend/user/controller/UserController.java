package com.imures.kaadbackend.user.controller;

import com.imures.kaadbackend.configuration.JwtService;
import com.imures.kaadbackend.user.controller.request.AuthenticationRequest;
import com.imures.kaadbackend.user.controller.request.PasswordRequest;
import com.imures.kaadbackend.user.controller.request.UserRequest;
import com.imures.kaadbackend.user.controller.response.AuthenticationResponse;
import com.imures.kaadbackend.user.controller.response.UserPutResponse;
import com.imures.kaadbackend.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.AccessDeniedException;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/user")
public class UserController {

    private final UserService userService;
    private final JwtService jwtService;

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> createUser(
            @RequestBody UserRequest request
    ){
        return new ResponseEntity<>(userService.createUser(request), HttpStatus.CREATED);
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/blog-image")
    public ResponseEntity<Void> changeBlogImage(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String jwtToken,
            @RequestPart("image") MultipartFile image
    ) throws IOException {
        String username = jwtService.extractUsername(jwtToken.substring(7));
        userService.updateBlogImage(image, username);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(value = "/login")
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

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/valid")
    public ResponseEntity<Void> valid(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token
    ){
        userService.validate(token.substring(7));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("passwordReset")
    public ResponseEntity<Void> changeUserPassword(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token,
            @RequestBody PasswordRequest password
    ) throws AccessDeniedException {
        userService.changeUserPassword(password, token.substring(7));
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping
    public ResponseEntity<UserPutResponse> updateUser(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token,
            @RequestPart("body")  UserRequest request,
            @RequestPart("image") @Nullable MultipartFile image
    ) throws IOException {
        return new ResponseEntity<>(userService.updateUser(request, image, token.substring(7)), HttpStatus.OK);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping(
            path = "photo",
            produces = MediaType.IMAGE_JPEG_VALUE
    )
    public ResponseEntity<byte[]> getUserImage(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token
    ) throws AccessDeniedException {
        return new ResponseEntity<>(userService.getUserPhoto(token.substring(7)), HttpStatus.OK);
    }

}