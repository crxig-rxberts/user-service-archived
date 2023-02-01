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

    @Test
    void whenSignUpUserIsPassedAValidAppUserThenExpectValidBehaviour() {
        assertTrue(confirmationTokenExistsInCTRepository(signUpValidUser()));
        assertTrue(appUserExistsInAppUserRepository() );

    }


    // throws if email does not exist


    // Signup user successfully signs up user

    //


    private final UserEntity validUserEntity = UserEntity.builder()
            .firstName("Mr")
            .lastName("Test")
            .displayName("Mr. Test")
            .password("password")
            .email("abcdef@gmail.com")
            .build();

    private boolean appUserExistsInAppUserRepository() {
        return userRepository.findByEmail(validUserEntity.getEmail()).isPresent();
    }

    private boolean confirmationTokenExistsInCTRepository(String token) {
        return confirmationTokenRepository.findByToken(token).isPresent();
    }

    private String signUpValidUser() {
        return registrationService.signUpUser(validUserEntity);
    }

    private boolean appUserValuesAreValid() {

        return true;
    }

}