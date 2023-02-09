package com.userservice.registration;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.userservice.registration.email.EmailSender;
import com.userservice.registration.email.EmailValidator;
import com.userservice.registration.token.ConfirmationTokenService;
import com.userservice.user.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@ExtendWith(MockitoExtension.class)
class RegistrationServiceTest {
    @Mock
    private EmailValidator emailValidator;
    @Mock
    private ConfirmationTokenService confirmationTokenService;
    @Mock
    private EmailSender emailSender;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @InjectMocks
    private RegistrationService registrationService;

    private RegistrationRequest registrationRequest;

    @BeforeEach
    void setUp() {
        registrationRequest =
                RegistrationRequest.builder()
                        .firstName("John")
                        .lastName("Doe")
                        .displayName("John Doe")
                        .email("johndoe@example.com")
                        .password("password")
                        .build();
    }

    @Test
    void test_register_validEmail() {
        when(emailValidator.test(any())).thenReturn(true);
        when(userRepository.findByEmail(any())).thenReturn(Optional.empty());
        when(bCryptPasswordEncoder.encode(any())).thenReturn("encryptedPassword");

        assertNotNull(registrationService.register(registrationRequest));
        verify(emailSender).send(any(), any());
        verify(userRepository).save(any());
        verify(confirmationTokenService).saveConfirmationToken(any());
    }
}