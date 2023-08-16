package com.userservice.model.request;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@RequiredArgsConstructor
public class LoginRequest {

    private final String email;
    private final String password;
}
