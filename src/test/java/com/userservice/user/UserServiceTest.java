package com.userservice.user;

import com.userservice.service.response.Response;
import com.userservice.service.response.ResponseBuilder;
import com.userservice.service.user.UserEntity;
import com.userservice.service.user.UserRepository;
import com.userservice.service.user.UserRequest;
import com.userservice.service.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRequest userRequest;
    @Mock
    private UserEntity userEntity;
    @Mock
    private ResponseBuilder responseBuilder;
    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserService userService;


    @BeforeEach
    void setUp() {
        userEntity = mock(UserEntity.class);
        userRequest = UserRequest.builder()
                .email("johndoe@example.com")
                .firstName("John")
                .lastName("Doe")
                .displayName("John Doe")
                .newEmail("johndoenew@example.com")
                .password("P&ssword123")
                .locked(true)
                .build();
    }

    @Test
    void updateUserCredentials_when_completeValidUserRequest_then_expectAllMethodsCalled() {
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(userEntity));
        when(bCryptPasswordEncoder.encode(any())).thenReturn("encoded_password");

        userService.updateUserCredentials(userRequest);

        verify(userRepository, times(1)).findByEmail(any());
        verify(userEntity, times(1)).setPassword(any());
        verify(bCryptPasswordEncoder, times(1)).encode(any());
        verify(userEntity, times(1)).setDisplayName(any());
        verify(userEntity, times(1)).setEmail(any());
        verify(userEntity, times(1)).setFirstName(any());
        verify(userEntity, times(1)).setLastName(any());
        verify(userEntity, times(1)).setLocked(any());
        verify(userRepository, times(1)).save(any());
        verify(responseBuilder, times(1)).buildResponse(isNull(), any());
        verifyNoMoreInteractions(userRepository, userEntity, bCryptPasswordEncoder, responseBuilder);
    }

    @Test
    void updateUserCredentials_when_userDoesNotExist_then_expect_notFoundResponse() {
        when(userRepository.findByEmail(any())).thenReturn(Optional.empty());

        userService.updateUserCredentials(userRequest);

        verify(userRepository, times(1)).findByEmail(any());
        verify(responseBuilder, times(1)).buildResponse(any(), isNull());

//        assertTrue(ex.getLogMsg().contains("User Entity not found with email: " + userRequest.getEmail()));
    }
}
