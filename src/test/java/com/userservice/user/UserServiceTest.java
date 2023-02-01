package com.userservice.user;

import com.userservice.registration.RegistrationService;
import com.userservice.registration.token.ConfirmationTokenRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
    // TODO: create test profile
@ActiveProfiles("local")
class UserServiceTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    ConfirmationTokenRepository confirmationTokenRepository;
    @Autowired
    RegistrationService registrationService;

}