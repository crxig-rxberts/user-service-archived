package com.userservice.user;

import com.userservice.registration.token.ConfirmationTokenRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
    // TODO: create test profile
@ActiveProfiles("local")
public class UserServiceTest {

    @Autowired
    private AppUserService appUserService;

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    ConfirmationTokenRepository confirmationTokenRepository;

    // private void loadUserByUsernameTest();


    // Will return a token
    // given a valid registration request
    // when signUpUser is called
    // then expect 200 response with toke

    @Test
    void whenSignUpUserIsPassedAValidAppUserThenExpectValidBehaviour() {
        assertTrue(confirmationTokenExistsInCTRepository(signUpValidUser()));
        assertTrue(appUserExistsInAppUserRepository() );

    }


    // throws if email does not exist


    // Signup user successfully signs up user

    //


    private final AppUser validAppUser = AppUser.builder()
            .firstName("Mr")
            .lastName("Test")
            .displayName("Mr. Test")
            .password("password")
            .email("abcdef@gmail.com")
            .build();

    private boolean appUserExistsInAppUserRepository() {
        return appUserRepository.findByEmail(validAppUser.getEmail()).isPresent();
    }

    private boolean confirmationTokenExistsInCTRepository(String token) {
        return confirmationTokenRepository.findByToken(token).isPresent();
    }

    private String signUpValidUser() {
        return appUserService.signUpUser(validAppUser);
    }

    private boolean appUserValuesAreValid() {

        return true;
    }

}