package com.userservice.controller;

import com.userservice.model.request.LoginRequest;
import com.userservice.model.response.LoginResponse;
import com.userservice.service.authentication.AuthenticationService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "api/")
@AllArgsConstructor
@Slf4j
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @PostMapping(path="/public/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        return authenticationService.login(loginRequest);
    }

//    @PostMapping(path="/auth/refresh")
//    public ResponseEntity<RefreshResponse> refresh(@RequestBody RefreshRequest refreshRequest) {
//        return userService.refresh(refreshRequest);
//    }
}
