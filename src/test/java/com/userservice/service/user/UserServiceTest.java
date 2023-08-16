package com.userservice.service.user;

import com.userservice.exception.NotFoundException;
import com.userservice.exception.UnauthorizedException;
import com.userservice.model.entity.UserEntity;
import com.userservice.model.request.UpdateDetailsRequest;
import com.userservice.model.request.UpdateEmailRequest;
import com.userservice.model.request.UpdatePasswordRequest;
import com.userservice.model.response.BaseResponse;
import com.userservice.model.response.mapper.ResponseMapper;
import com.userservice.model.response.mapper.ResponseStatus;
import com.userservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserServiceTest {

    private static final String EMAIL = "test@email.com";
    private static final String PASSWORD = "password123";
    private static final String ENCRYPTED_PASSWORD = "$2a$12$abc.def";
    private static final String NEW_EMAIL = "new@email.com";
    private static final String NEW_PASSWORD = "newPassword123";
    private static final String DISPLAY_NAME = "displayName";
    private static final String FIRST_NAME = "firstName";
    private static final String LAST_NAME = "lastName";

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Mock
    private ResponseMapper responseMapper;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testReturnUser() {
        UserEntity userEntity = new UserEntity();
        when(userRepository.findUserByEmail(EMAIL)).thenReturn(Optional.of(userEntity));
        userService.returnUser(EMAIL);
        verify(responseMapper).buildUserResponse(userEntity);
    }

    @Test
    void testUpdatePassword() {
        UserEntity userEntity = mockUserEntityWithPassword();
        when(bCryptPasswordEncoder.matches(PASSWORD, ENCRYPTED_PASSWORD)).thenReturn(true);
        when(bCryptPasswordEncoder.encode(NEW_PASSWORD)).thenReturn(ENCRYPTED_PASSWORD);
        when(userRepository.save(userEntity)).thenReturn(userEntity);

        ResponseEntity<BaseResponse> response = userService.updatePassword(new UpdatePasswordRequest(EMAIL, PASSWORD, NEW_PASSWORD));
        assertEquals(ResponseStatus.SUCCESS, Objects.requireNonNull(response.getBody()).getStatus());
        assertEquals(ENCRYPTED_PASSWORD, userEntity.getPassword());

    }

    @Test
    void testUpdateEmail() {
        UserEntity userEntity = mockUserEntityWithPassword();
        when(bCryptPasswordEncoder.matches(PASSWORD, ENCRYPTED_PASSWORD)).thenReturn(true);
        when(userRepository.save(userEntity)).thenReturn(userEntity);

        ResponseEntity<BaseResponse> response = userService.updateEmail(new UpdateEmailRequest(EMAIL, PASSWORD, NEW_EMAIL));

        assertEquals(ResponseStatus.SUCCESS, Objects.requireNonNull(response.getBody()).getStatus());
        assertEquals(NEW_EMAIL, userEntity.getEmail());
    }

    @Test
    void testUpdateDetails() {
        UserEntity userEntity = mockUserEntityWithPassword();
        when(bCryptPasswordEncoder.matches(PASSWORD, ENCRYPTED_PASSWORD)).thenReturn(true);
        when(userRepository.save(userEntity)).thenReturn(userEntity);
        ResponseEntity<BaseResponse> response = userService.updateDetails(new UpdateDetailsRequest(EMAIL, PASSWORD, DISPLAY_NAME, "firstName", "lastName"));

        assertEquals(ResponseStatus.SUCCESS, Objects.requireNonNull(response.getBody()).getStatus());
        assertEquals(DISPLAY_NAME, userEntity.getDisplayName());
        assertEquals(FIRST_NAME, userEntity.getFirstName());
        assertEquals(LAST_NAME, userEntity.getLastName());

    }

    @Test
    void testPasswordNotMatchingThrowsUnauthorizedException() {
        mockUserEntityWithPassword();
        when(bCryptPasswordEncoder.matches(PASSWORD, ENCRYPTED_PASSWORD)).thenReturn(false);
        assertThrows(UnauthorizedException.class, () -> userService.updatePassword(new UpdatePasswordRequest(EMAIL, PASSWORD, NEW_PASSWORD)));
    }

    @Test
    void testUserNotFound() {
        when(userRepository.findUserByEmail(EMAIL)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> userService.returnUser(EMAIL));
    }

    private UserEntity mockUserEntityWithPassword() {
        UserEntity userEntity = new UserEntity();
        userEntity.setEmail(EMAIL);
        userEntity.setPassword(ENCRYPTED_PASSWORD);
        when(userRepository.findUserByEmail(EMAIL)).thenReturn(Optional.of(userEntity));
        return userEntity;
    }
}

