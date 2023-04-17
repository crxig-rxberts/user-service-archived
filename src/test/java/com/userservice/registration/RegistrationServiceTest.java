package com.userservice.registration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.userservice.exception.ConflictException;
import com.userservice.service.registration.RegistrationRequest;
import com.userservice.service.response.Response;
import com.userservice.service.registration.RegistrationService;
import com.userservice.service.registration.email.EmailSender;

import com.userservice.service.registration.token.ConfirmationTokenRepository;
import com.userservice.service.response.ResponseMapper;
import com.userservice.service.response.ResponseStatus;
import com.userservice.service.user.UserEntity;
import com.userservice.service.user.UserRepository;

import java.util.Objects;
import java.util.Optional;

import com.userservice.service.user.UserRole;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.validation.ConstraintViolationException;

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
        when(userRepository.findByEmail(any())).thenReturn(Optional.empty());
        when(bCryptPasswordEncoder.encode(any())).thenReturn("password");
        when(responseMapper.buildResponse(any(UserEntity.class)))
                .thenReturn(ResponseEntity.ok(new Response(ResponseStatus.SUCCESS, null, userEntity)));

        ResponseEntity<Response> response = registrationService.register(registrationRequest);

        verify(emailSender, times(1)).send(any(), any());
        verify(userRepository, times(1)).save(any());
        verify(userRepository, times(1)).findByEmail(any());
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
    void register_when_emailAlreadyExists_then_expectBadRequest() {
        UserEntity existingUser = new UserEntity("John", "Doe", "johndoe", "johndoe@example.com", "password", UserRole.USER);
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(existingUser));
        when(responseMapper.buildResponse(any(ConflictException.class)))
                .thenReturn(ResponseEntity.status(HttpStatus.CONFLICT).body(new Response(ResponseStatus.CONFLICT, "User already exists in DB.",  null)));

        ResponseEntity<Response> response = registrationService.register(registrationRequest);

        verify(userRepository, times(1)).findByEmail(any());
        verify(userRepository, never()).save(any());
        verify(bCryptPasswordEncoder, never()).encode(any());
        verify(emailSender, never()).send(any(), any());
        verify(confirmationTokenRepository, never()).save(any());
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("User already exists in DB.", Objects.requireNonNull(response.getBody()).getErrorMessage());
        assertNull(response.getBody().getUserEntity());
    }

    @Test
    void register_when_requestIsInvalid_then_expectConstraintViolationResponse() {
        RegistrationRequest invalidRequest =
                RegistrationRequest.builder()
                        .firstName("John")
                        .lastName("Doe")
                        .displayName("JohnDoe")
                        .email("invalid_email_address")
                        .password("P$ssword123")
                        .build();
        when(responseMapper.buildResponse(any(ConstraintViolationException.class)))
                .thenReturn(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(ResponseStatus.BAD_REQUEST, "Email must be a well-formed email address", null)));

        ResponseEntity<Response> response = registrationService.register(invalidRequest);

        verify(emailSender, never()).send(any(), any());
        verify(userRepository, never()).save(any());
        verify(userRepository, never()).findByEmail(any());
        verify(bCryptPasswordEncoder, never()).encode(any());
        verify(confirmationTokenRepository, never()).save(any());
        assertEquals(ResponseStatus.BAD_REQUEST, Objects.requireNonNull(response.getBody()).getStatus());
        assertTrue(response.getBody().getErrorMessage().contains("must be a well-formed email address"));
        assertNull(response.getBody().getUserEntity());
    }
}