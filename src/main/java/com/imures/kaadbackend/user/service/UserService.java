package com.imures.kaadbackend.user.service;


import com.imures.kaadbackend.configuration.JwtService;
import com.imures.kaadbackend.roles.entity.Role;
import com.imures.kaadbackend.roles.repository.RoleRepository;
import com.imures.kaadbackend.user.controller.request.AuthenticationRequest;
import com.imures.kaadbackend.user.controller.request.PasswordRequest;
import com.imures.kaadbackend.user.controller.request.UserRequest;
import com.imures.kaadbackend.user.controller.response.AuthenticationResponse;
import com.imures.kaadbackend.user.controller.response.UserPutResponse;
import com.imures.kaadbackend.user.entity.User;
import com.imures.kaadbackend.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public AuthenticationResponse createUser(UserRequest details){
        boolean isExists = true;
        try {
            userRepository.findUserByEmail(details.getEmail())
                    .orElseThrow(()-> new RuntimeException("User does not exists"));
        } catch (RuntimeException e){
            isExists = false;
        }
        if(isExists) throw new RuntimeException("User already exists with this login");

        User newUser = new User();

        BeanUtils.copyProperties(details, newUser);
        newUser.setEmail(newUser.getEmail().trim().toLowerCase());
        newUser.setIsEnabled(true);
        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));

        Set<Role> roles = new HashSet<>();
        for(Long roleId : details.getRoles()){
            roles.add(
                    roleRepository
                            .findById(roleId)
                            .orElseThrow(()-> new EntityNotFoundException("Role " + roleId + " does not exists!"))
            );
        }

        newUser.setRoles(roles);

        User saved = userRepository.save(newUser);
        String jwtToken = jwtService.generateToken(saved);
        String refreshToken = jwtService.generateRefreshToken(saved);

        return  AuthenticationResponse.builder()
                .token(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Transactional(readOnly = true)
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        request.setLogin(request.getLogin().trim().toLowerCase());
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getLogin(),
                        request.getPassword()
                )
        );

        User user = userRepository.findUserByEmail(request.getLogin())
                .orElseThrow(()-> new UsernameNotFoundException("User was not found"));

        String jwtToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return  AuthenticationResponse.builder()
                .token(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Transactional(readOnly = true)
    public AuthenticationResponse refresh(String token) {
        String userMail = jwtService.extractRefreshedUsername(token);
        User user = userRepository.findUserByEmail(userMail)
                .orElseThrow(()-> new UsernameNotFoundException("User does not exists"));

        if(!jwtService.isRefreshTokenValid(token, user)){
            throw new BadCredentialsException("Invalid token");
        }
        String jwtToken = jwtService.generateToken(user);
        String refreshedToken = jwtService.generateRefreshToken(user);

        return AuthenticationResponse.builder()
                .token(jwtToken)
                .refreshToken(refreshedToken)
                .build();
    }

    @Transactional(readOnly = true)
    public void validate(String token) {
        String userMail = jwtService.extractUsername(token);
        User user = userRepository.findUserByEmail(userMail)
                .orElseThrow(()-> new UsernameNotFoundException("User does not exists"));
        if(!jwtService.isTokenValid(token, user)){ // ??????????????
            throw new BadCredentialsException("Invalid token");
        }
    }

    @Transactional
    public void updateBlogImage(MultipartFile image, String userMail) throws IOException {
        User user = userRepository.findUserByEmail(userMail)
                .orElseThrow(()-> new UsernameNotFoundException("User does not exists"));
        user.setBlogImage(image.getBytes());
        userRepository.save(user);
    }

    @Transactional
    public byte[] getUserPhoto(String token) throws AccessDeniedException {
        String userMail = jwtService.extractUsername(token);
        User user = userRepository.findUserByEmail(userMail)
                .orElseThrow(()-> new UsernameNotFoundException("User does not exists"));
        if(!jwtService.isTokenValid(token, user)){ // ??????????????
            throw new AccessDeniedException("Invalid token");
        }

        if(user.getBlogImage() == null){
            throw new EntityNotFoundException("User does not have blog image");
        }
        return user.getBlogImage();
    }

    @Transactional
    public void changeUserPassword(PasswordRequest password, String token) throws AccessDeniedException {
        if(jwtService.isTokenExpired(token)){
            throw new AccessDeniedException("Session expired");
        }
        String userMail = jwtService.extractUsername(token);

        User user = userRepository.findUserByEmail(userMail)
                .orElseThrow(()-> new UsernameNotFoundException("User does not exists"));
        user.setPassword(passwordEncoder.encode(password.getPassword().trim()));
        userRepository.save(user);
    }

    @Transactional
    public UserPutResponse updateUser(UserRequest request, MultipartFile image, String token) throws IOException {
        if(jwtService.isTokenExpired(token)){
            throw new AccessDeniedException("Session expired");
        }
        String userMail = jwtService.extractUsername(token);

        User user = userRepository.findUserByEmail(userMail)
                .orElseThrow(()-> new UsernameNotFoundException("User does not exists"));

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());

        if(image != null){
            user.setBlogImage(image.getBytes());
        }

        userRepository.save(user);

        UserPutResponse response = new UserPutResponse();
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setEmail(user.getEmail());

        return response;
    }
}