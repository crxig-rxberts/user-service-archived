package com.userservice.user;

import com.userservice.exception.NotFoundException;
import com.userservice.service.response.Response;
import com.userservice.service.response.ResponseBuilder;
import com.userservice.service.response.ResponseStatus;
import com.userservice.service.user.UserEntity;
import com.userservice.service.user.UserRepository;
import com.userservice.service.user.UserRequest;
import com.userservice.service.user.UserRole;
import com.userservice.service.user.UserService;
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
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private ResponseBuilder responseBuilder;
    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserService userService;

    private UserRequest userRequest;
    private UserEntity userEntity;

    @BeforeEach
    void setUp() {
        userRequest = UserRequest.builder()
                .email("johndoe@example.com")
                .password("P$ssWrd123!")
                .displayName("JohnDoe")
                .newEmail("newjohndoe@example.com")
                .firstName("John")
                .lastName("Doe")
                .locked(true)
                .build();
        userEntity = new UserEntity("John", "Doe", "johndoe", "johndoe@example.com", "password", UserRole.USER);
    }

    @Test
    @SneakyThrows
    void updateUserCredentials_when_userExists_then_expectValidBehaviour() {
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(userEntity));
        when(bCryptPasswordEncoder.encode(any())).thenReturn("encodedPassword");
        when(userRepository.save(any())).thenReturn(userEntity);
        when(responseBuilder.buildResponse(any(UserEntity.class)))
                .thenReturn(ResponseEntity.ok(new Response(ResponseStatus.SUCCESS, null, userEntity)));


        ResponseEntity<Response> response = userService.updateUserCredentials(userRequest);

        verify(userRepository, times(1)).findByEmail(any());
        verify(bCryptPasswordEncoder, times(1)).encode(any());
        verify(userRepository, times(1)).save(any());
        assertNull(Objects.requireNonNull(response.getBody()).getErrorMessage());
        assertEquals(ResponseStatus.SUCCESS, response.getBody().getStatus());
        assertEquals(userEntity, response.getBody().getUserEntity());
    }

    @Test
    @SneakyThrows
    void updateUserCredentials_when_userNotFound_then_expectNotFoundException() {
        when(userRepository.findByEmail(any())).thenReturn(Optional.empty());
        when(responseBuilder.buildResponse(any(NotFoundException.class)))
                .thenReturn(ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response(ResponseStatus.NOT_FOUND, "No user exists in DB with given credentials.", null)));

        ResponseEntity<Response> response = userService.updateUserCredentials(userRequest);

        verify(userRepository, times(1)).findByEmail(any());
        verify(bCryptPasswordEncoder, never()).encode(any());
        verify(userRepository, never()).save(any());
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("No user exists in DB with given credentials.", Objects.requireNonNull(response.getBody()).getErrorMessage());
        assertNull(response.getBody().getUserEntity());
    }

    @Test
    @SneakyThrows
    void updateUserCredentials_when_requestIsInvalid_then_expectConstraintViolationResponse() {
        UserRequest invalidRequest = UserRequest.builder().email("johndoe@example.com").newEmail("invalidEmail").build();
        when(responseBuilder.buildResponse(any(ConstraintViolationException.class)))
                .thenReturn(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(ResponseStatus.BAD_REQUEST, "Email must be a well-formed email address", null)));

        ResponseEntity<Response> response = userService.updateUserCredentials(invalidRequest);

        verify(userRepository, never()).findByEmail(any());
        verify(bCryptPasswordEncoder, never()).encode(any());
        verify(userRepository, never()).save(any());
        assertEquals(ResponseStatus.BAD_REQUEST, Objects.requireNonNull(response.getBody()).getStatus());
        assertTrue(response.getBody().getErrorMessage().contains("must be a well-formed email address"));
        assertNull(response.getBody().getUserEntity());
    }
}
