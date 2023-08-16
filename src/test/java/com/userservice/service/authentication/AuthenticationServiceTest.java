package com.userservice.service.authentication;

import com.userservice.exception.NotFoundException;
import com.userservice.exception.UnauthorizedException;
import com.userservice.model.entity.UserEntity;
import com.userservice.model.request.LoginRequest;
import com.userservice.model.response.LoginResponse;
import com.userservice.repository.UserRepository;
import com.userservice.security.jwt.JwtTokenUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

class AuthenticationServiceTest {

    private static final String EMAIL = "test@email.com";
    private static final String PASSWORD = "password123";
    private static final String ENCRYPTED_PASSWORD = "$2a$12$abc.def";
    private static final String JWT_TOKEN = "sample.jwt.token";
    private static final String REFRESH_TOKEN = "refreshToken";

    @InjectMocks
    private AuthenticationService authenticationService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Mock
    private JwtTokenUtil jwtTokenUtil;

    @Mock
    private UserDetails userDetails;

    @Mock
    private UserEntity userEntity;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testLoginSuccess() {
        mockUserDetailsWithPassword();

        when(jwtTokenUtil.generateToken(userDetails)).thenReturn(JWT_TOKEN);
        when(bCryptPasswordEncoder.matches(PASSWORD, ENCRYPTED_PASSWORD)).thenReturn(true);

        ResponseEntity<LoginResponse> response = authenticationService.login(new LoginRequest(EMAIL, PASSWORD));

        assertEquals(JWT_TOKEN, Objects.requireNonNull(response.getBody()).getJwtToken());
        assertEquals(REFRESH_TOKEN, response.getBody().getRefreshToken());
    }

    @Test
    void testLoginUserNotFound() {
        when(userRepository.findUserByEmail(EMAIL)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> authenticationService.login(new LoginRequest(EMAIL, PASSWORD)));
    }

    @Test
    void testLoginPasswordNotMatchingThrowsUnauthorizedException() {
        mockUserDetailsWithPassword();
        when(bCryptPasswordEncoder.matches(PASSWORD, ENCRYPTED_PASSWORD)).thenReturn(false);

        assertThrows(UnauthorizedException.class, () -> authenticationService.login(new LoginRequest(EMAIL, PASSWORD)));
    }

    private void mockUserDetailsWithPassword() {
        when(userDetails.getUsername()).thenReturn(EMAIL);
        when(userDetails.getPassword()).thenReturn(ENCRYPTED_PASSWORD);
        when(userRepository.findUserByEmail(EMAIL)).thenReturn(Optional.of(userEntity));
        when(userRepository.findByEmail(EMAIL)).thenReturn(userDetails);
    }
}
