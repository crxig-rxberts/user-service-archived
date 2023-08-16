package com.userservice.model.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class LoginResponse {

    private final String jwtToken;
    private final String refreshToken;
}
