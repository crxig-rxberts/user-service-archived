package com.userservice.registration;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.userservice.service.registration.RegistrationRequest;
import com.userservice.service.response.Response;
import com.userservice.service.registration.RegistrationService;
import com.userservice.service.registration.email.EmailSender;

import com.userservice.service.registration.token.ConfirmationTokenRepository;
import com.userservice.service.response.ResponseBuilder;
import com.userservice.service.user.UserEntity;
import com.userservice.service.user.UserRepository;

import java.util.Objects;
import java.util.Optional;

import com.userservice.service.user.UserRole;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Slf4j
@ExtendWith(MockitoExtension.class)
class RegistrationServiceTest {
    @Mock
    private ConfirmationTokenRepository confirmationTokenRepository;
    @Mock
    private EmailSender emailSender;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Mock
    private ResponseBuilder responseBuilder;
    @InjectMocks
    private RegistrationService registrationService;

    private RegistrationRequest registrationRequest;

    @BeforeEach
    void setUp() {
        registrationRequest =
                RegistrationRequest.builder()
                        .firstName("John")
                        .lastName("Doe")
                        .displayName("JohnDoe")
                        .email("johndoe@example.com")
                        .password("P$ssword123")
                        .build();
    }

    @Test
    void register_when_requestIsValid_then_expectValidBehaviour() {
        registrationService.register(registrationRequest);
        verify(emailSender, times(1)).send(any(), any());
        verify(userRepository, times(1)).save(any());
        verify(userRepository, times(1)).findByEmail(any());
        verify(bCryptPasswordEncoder, times(1)).encode(any());
        verify(confirmationTokenRepository, times(1)).save(any());
    }

    @Test
    void register_when_emailAlreadyExists_then_expectBadRequest() {
        UserEntity existingUser = new UserEntity("John", "Doe", "johndoe", "johndoe@example.com", "password", UserRole.USER);
        when(userRepository.findByEmail(registrationRequest.getEmail())).thenReturn(Optional.of(existingUser));

        registrationService.register(registrationRequest);
        verify(userRepository, times(1)).findByEmail(any());
        verify(userRepository, never()).save(any());
        verify(bCryptPasswordEncoder, never()).encode(any());
        verify(emailSender, never()).send(any(), any());
        verify(confirmationTokenRepository, never()).save(any());
    }
}