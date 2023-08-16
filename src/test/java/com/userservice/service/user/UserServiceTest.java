package com.userservice.service.user;

import com.userservice.exception.NotFoundException;
import com.userservice.exception.UnauthorizedException;
import com.userservice.model.request.LoginRequest;
import com.userservice.model.entity.UserEntity;
import com.userservice.model.response.LoginResponse;
import com.userservice.model.response.RefreshResponse;
import com.userservice.model.response.mapper.ResponseMapper;
import com.userservice.model.request.UserRequest;
import com.userservice.repository.UserRepository;
import com.userservice.security.jwt.JwtTokenUtil;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    private static final String VALID_EMAIL = "johndoe@example.com";
    private static final String VALID_PASSWORD = "Password";
    private static final String VALID_DISPLAY_NAME = "JohnDoe";
    private static final String VALID_NEW_EMAIL = "new@example.com";
    private static final String INVALID_PASSWORD = "ABC";
    private static final String FIRST_NAME = "John";
    private static final String LAST_NAME = "Doe";
    private static final String HASHED_PASSWORD = "hashedPassword";

    @Mock
    private ResponseMapper responseMapper;
    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Mock
    private UserRepository userRepository;
    @Mock
    private LoginRequest loginRequest;
    @Mock
    private UserDetails userDetails;
    @Mock
    private JwtTokenUtil jwtTokenUtil;
    @Mock
    private UserRequest userRequest;
    @Mock
    private UserEntity userEntity;
    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
    }

    @Test
    @SneakyThrows
    void updateUserCredentials_when_userExists_then_expectValidBehaviour() {
        when(userRepository.findUserByEmail(any())).thenReturn(Optional.of(userEntity));
        when(bCryptPasswordEncoder.encode(any())).thenReturn(HASHED_PASSWORD);
        when(userRepository.save(any())).thenReturn(userEntity);

        when(userRequest.getEmail()).thenReturn(VALID_EMAIL);
        when(userRequest.getPassword()).thenReturn(VALID_PASSWORD);
        when(userRequest.getDisplayName()).thenReturn(VALID_DISPLAY_NAME);
        when(userRequest.getNewEmail()).thenReturn(VALID_NEW_EMAIL);
        when(userRequest.getFirstName()).thenReturn(FIRST_NAME);
        when(userRequest.getLastName()).thenReturn(LAST_NAME);
        when(userRequest.getLocked()).thenReturn(true);

        userService.updateUserCredentials(userRequest);

        verify(userRepository, times(1)).findUserByEmail(userRequest.getEmail());
        verify(bCryptPasswordEncoder, times(1)).encode(userRequest.getPassword());
        verify(userEntity, times(1)).setEmail(VALID_NEW_EMAIL);
        verify(userEntity, times(1)).setPassword(HASHED_PASSWORD);
        verify(userEntity, times(1)).setDisplayName(VALID_DISPLAY_NAME);
        verify(userEntity, times(1)).setFirstName(FIRST_NAME);
        verify(userEntity, times(1)).setLastName(LAST_NAME);
        verify(userEntity, times(1)).setLocked(true);
        verify(userRepository, times(1)).save(userEntity);
    }


    @Test
    @SneakyThrows
    void updateUserCredentials_when_userNotFound_then_expectNotFoundException() {
        when(userRepository.findUserByEmail(any())).thenReturn(Optional.empty());
        when(userRequest.getEmail()).thenReturn(VALID_EMAIL);

        assertThrows(NotFoundException.class, () -> userService.updateUserCredentials(userRequest));

        verify(userRepository, times(1)).findUserByEmail(any());
        verify(bCryptPasswordEncoder, never()).encode(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    void login_when_userExistsAndPasswordMatches_then_returnsJwt() {
        when(loginRequest.getEmail()).thenReturn(VALID_EMAIL);
        when(userDetails.getPassword()).thenReturn(HASHED_PASSWORD);
        when(loginRequest.getPassword()).thenReturn(VALID_PASSWORD);
        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(userDetails);
        when(userRepository.findUserByEmail(loginRequest.getEmail())).thenReturn(Optional.of(userEntity));
        when(bCryptPasswordEncoder.matches(loginRequest.getPassword(), userDetails.getPassword())).thenReturn(true);
        when(jwtTokenUtil.generateToken(userDetails)).thenReturn("generatedJwtToken");

        ResponseEntity<LoginResponse> response = userService.login(loginRequest);

        assertEquals("generatedJwtToken", Objects.requireNonNull(response.getBody()).getRefreshToken());
        verify(userRepository, times(1)).findUserByEmail(loginRequest.getEmail());
        verify(bCryptPasswordEncoder, times(1)).matches(loginRequest.getPassword(), userDetails.getPassword());
        verify(jwtTokenUtil, times(1)).generateToken(userDetails);
    }

    @Test
    void login_when_userExistsAndPasswordNotMatch_then_throwsUnauthorizedException() {
        when(userDetails.getPassword()).thenReturn(HASHED_PASSWORD);
        when(loginRequest.getEmail()).thenReturn(VALID_EMAIL);
        when(loginRequest.getPassword()).thenReturn(INVALID_PASSWORD);
        when(userRepository.findUserByEmail(any())).thenReturn(Optional.of(userEntity));
        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(userDetails);
        when(bCryptPasswordEncoder.matches(any(), any())).thenReturn(false);

        assertThrows(UnauthorizedException.class, () -> userService.login(loginRequest));

        verify(userRepository, times(1)).findUserByEmail(loginRequest.getEmail());
        verify(bCryptPasswordEncoder, times(1)).matches(loginRequest.getPassword(), userDetails.getPassword());
        verify(jwtTokenUtil, never()).generateToken(any());
    }

    @Test
    void login_when_userNotFound_then_throwsNotFoundException() {
        when(userRepository.findUserByEmail(any())).thenReturn(Optional.empty());
        when(loginRequest.getEmail()).thenReturn(VALID_EMAIL);

        assertThrows(NotFoundException.class, () -> userService.login(loginRequest));

        verify(userRepository, times(1)).findUserByEmail(loginRequest.getEmail());
        verify(bCryptPasswordEncoder, never()).matches(any(), any());
        verify(jwtTokenUtil, never()).generateToken(any());
    }

}
