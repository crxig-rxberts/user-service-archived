package com.userservice.service.authentication;

import com.userservice.exception.NotFoundException;
import com.userservice.exception.UnauthorizedException;
import com.userservice.model.request.LoginRequest;
import com.userservice.model.response.LoginResponse;
import com.userservice.repository.UserRepository;
import com.userservice.security.jwt.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final UserRepository userRepository;
    private final JwtTokenUtil jwtTokenUtil;

    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        checkUserExists(loginRequest.getEmail());

        UserDetails userDetails = userRepository.findByEmail(loginRequest.getEmail());
        authenticateGivenPassword(loginRequest.getPassword(), userDetails.getPassword());

        return ResponseEntity.ok(new LoginResponse(jwtTokenUtil.generateToken(userDetails), "refreshToken"));
    }

    private void checkUserExists(String email) {
        if (userRepository.findUserByEmail(email).isEmpty()) {
            throw new NotFoundException("No user exists in DB with given credentials.");
        }
    }

    private void authenticateGivenPassword(String givenPassword, String storedPassword) {
        if (!bCryptPasswordEncoder.matches(givenPassword, storedPassword)) {
            throw new UnauthorizedException("Given Password incorrect for User.");
        }
    }
}
