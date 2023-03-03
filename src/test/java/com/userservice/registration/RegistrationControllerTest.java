package com.userservice.registration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.userservice.service.registration.RegistrationRequest;
import com.userservice.service.registration.RegistrationService;
import com.userservice.service.registration.token.ConfirmationToken;
import com.userservice.service.registration.token.ConfirmationTokenService;
import com.userservice.service.user.UserEntity;
import com.userservice.service.user.UserRepository;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("local")
class RegistrationControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;
    private RegistrationRequest registrationRequest;

    @Test
    @SneakyThrows
    void registerValidRequestReturnsOkResponseAndRegistersUser() {
        registrationRequest = new RegistrationRequest("John", "Doe", "JohnDoe", "P$ssWord123", "jdoe@test.com");

        mockMvc.perform(post("/api/v1/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(registrationRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").exists())
                .andExpect(jsonPath("$.errorMessage").doesNotExist())
                .andExpect(jsonPath("$.userEntity").exists());

        assertTrue(userRepository.findByEmail(registrationRequest.getEmail()).isPresent());
    }

    @Test
    @SneakyThrows
    void registerInvalidRequestReturnsBadRequest() {
        registrationRequest = new RegistrationRequest();
        mockMvc.perform(post("/api/v1/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(registrationRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").exists())
                .andExpect(jsonPath("$.errorMessage").exists())
                .andExpect(jsonPath("$.userEntity").doesNotExist());

        assertFalse(userRepository.findByEmail(registrationRequest.getEmail()).isPresent());
    }

    @Test
    @SneakyThrows
    void registerRequestForExistingUserReturnsConflict() {
        registrationRequest = new RegistrationRequest("John", "Doe", "JohnDoe", "P$ssWord123", "jdoe@test.com");
        mockMvc.perform(post("/api/v1/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(registrationRequest)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").exists())
                .andExpect(jsonPath("$.errorMessage").exists())
                .andExpect(jsonPath("$.userEntity").doesNotExist());

        assertTrue(userRepository.findByEmail(registrationRequest.getEmail()).isPresent());
    }
}
