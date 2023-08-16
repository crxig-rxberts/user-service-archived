package com.userservice.controller;

import com.userservice.exception.NotFoundException;
import com.userservice.model.request.RefreshRequest;
import com.userservice.model.response.LoginResponse;
import com.userservice.model.response.RefreshResponse;
import com.userservice.model.response.UserResponse;
import com.userservice.model.request.LoginRequest;
import com.userservice.model.request.UserRequest;
import com.userservice.service.user.UserService;
import com.userservice.validator.RequestValidator;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "api")
@AllArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping(path="/public/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        return userService.login(loginRequest);
    }

    @GetMapping("/auth/user")
    public ResponseEntity<UserResponse> getUser(@RequestParam String email) throws NotFoundException {
        return userService.returnUser(email);
    }

    @PutMapping("/auth/update")
    public ResponseEntity<UserResponse> updateUserCredentials(@RequestBody UserRequest request) {
        RequestValidator.validateRequest(request);
        return userService.updateUserCredentials(request);
    }

//    @PostMapping(path="/auth/refresh")
//    public ResponseEntity<RefreshResponse> refresh(@RequestBody RefreshRequest refreshRequest) {
//        return userService.refresh(refreshRequest);
//    }
}
