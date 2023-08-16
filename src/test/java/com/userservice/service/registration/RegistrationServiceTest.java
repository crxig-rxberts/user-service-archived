package com.userservice.service.registration;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.userservice.exception.ConflictException;
import com.userservice.model.request.RegistrationRequest;
import com.userservice.model.response.UserResponse;
import com.userservice.service.registration.email.EmailSender;

import com.userservice.repository.ConfirmationTokenRepository;
import com.userservice.model.response.mapper.ResponseMapper;
import com.userservice.model.response.mapper.ResponseStatus;
import com.userservice.model.entity.UserEntity;
import com.userservice.repository.UserRepository;

import java.util.Objects;
import java.util.Optional;

import com.userservice.model.entity.UserRole;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

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
    private ResponseMapper responseMapper;
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
    @SneakyThrows
    void register_when_requestIsValid_then_expectValidBehaviour() {
        UserEntity userEntity = new UserEntity("John", "Doe", "JohnDoe", "johndoe@example.com", "password", UserRole.USER);
        when(userRepository.findUserByEmail(any())).thenReturn(Optional.empty());
        when(bCryptPasswordEncoder.encode(any())).thenReturn("password");
        when(responseMapper.buildUserResponse(any(UserEntity.class)))
                .thenReturn(ResponseEntity.ok(UserResponse.builder()
                        .status(ResponseStatus.SUCCESS)
                        .userEntity(userEntity)
                        .build()));

        ResponseEntity<UserResponse> response = registrationService.register(registrationRequest);

        verify(emailSender, times(1)).send(any(), any());
        verify(userRepository, times(1)).save(any());
        verify(userRepository, times(1)).findUserByEmail(any());
        verify(bCryptPasswordEncoder, times(1)).encode(any());
        verify(confirmationTokenRepository, times(1)).save(any());
        assertEquals(ResponseStatus.SUCCESS, Objects.requireNonNull(response.getBody()).getStatus());
        assertNull(response.getBody().getErrorMessage());
        assertEquals(registrationRequest.getFirstName(), response.getBody().getUserEntity().getFirstName());
        assertEquals(registrationRequest.getLastName(), response.getBody().getUserEntity().getLastName());
        assertEquals(registrationRequest.getDisplayName(), response.getBody().getUserEntity().getDisplayName());
        assertEquals(registrationRequest.getEmail(), response.getBody().getUserEntity().getEmail());
    }

    @Test
    void register_when_emailAlreadyExists_then_throwConflictException() {
        when(userRepository.findUserByEmail(any())).thenThrow(new ConflictException("User already exists in DB. CorrelationId."));

        assertThrows(ConflictException.class, () -> registrationService.register(registrationRequest));

        verify(userRepository, times(1)).findUserByEmail(any());
        verify(userRepository, never()).save(any());
        verify(bCryptPasswordEncoder, never()).encode(any());
        verify(emailSender, never()).send(any(), any());
        verify(confirmationTokenRepository, never()).save(any());
    };
}