package com.userservice.controller;

import com.userservice.exception.NotFoundException;
import com.userservice.model.request.UpdateDetailsRequest;
import com.userservice.model.request.UpdateEmailRequest;
import com.userservice.model.request.UpdatePasswordRequest;
import com.userservice.model.response.BaseResponse;
import com.userservice.model.response.UserResponse;
import com.userservice.service.user.UserService;
import com.userservice.validator.RequestValidator;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "api/auth")
@AllArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/user")
    public ResponseEntity<UserResponse> getUser(@RequestParam String email) throws NotFoundException {
        return userService.returnUser(email);
    }

    @PutMapping("/update/details")
    public ResponseEntity<BaseResponse> updateDetails(@RequestBody UpdateDetailsRequest request) {
        RequestValidator.validateRequest(request);
        return userService.updateDetails(request);
    }

    @PutMapping("/update/email")
    public ResponseEntity<BaseResponse> updateEmail(@RequestBody UpdateEmailRequest request) {
        RequestValidator.validateRequest(request);
        return userService.updateEmail(request);
    }

    @PutMapping("/update/password")
    public ResponseEntity<BaseResponse> updatePassword(@RequestBody UpdatePasswordRequest request) {
        RequestValidator.validateRequest(request);
        return userService.updatePassword(request);
    }

}
