package com.userservice.user;

import com.userservice.user.UserRepository;
import com.userservice.user.UserRequest;
import com.userservice.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    private UserService userService;
    private UserRequest userRequest;
    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Mock
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userService = new UserService(userRepository, bCryptPasswordEncoder);
        userRequest = UserRequest.builder()
                .email("johndoe@example.com")
                .firstName("John")
                .lastName("Doe")
                .displayName("John Doe")
                .newEmail("johndoenew@example.com")
                .password("password")
                .build();
    }

    @Test
    void updateUserCredentials_validUserRequest_userDataUpdated() {
        when(userRepository.findByEmail(userRequest.getEmail())).thenReturn(Optional.of(new UserEntity()));
        when(bCryptPasswordEncoder.encode(userRequest.getPassword())).thenReturn("encoded_password");

        userService.updateUserCredentials(userRequest);

        verify(userRepository, times(1)).findByEmail(userRequest.getEmail());
        verify(bCryptPasswordEncoder, times(1)).encode(userRequest.getPassword());
        verify(userRepository, times(1)).save(any(UserEntity.class));
    }
}
