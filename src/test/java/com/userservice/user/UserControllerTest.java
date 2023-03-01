package com.userservice.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.userservice.service.user.UserEntity;
import com.userservice.service.user.UserRepository;
import com.userservice.service.user.UserRequest;
import com.userservice.service.user.UserRole;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("local")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private UserEntity userEntity;
    private UserRequest userRequest;



    @BeforeEach
    void setUp() {
        userRequest = new UserRequest();
    }

    @AfterEach
    void close() {
        if (userEntity != null) {
            userRepository.delete(userEntity);
        }
    }

    @Test
    void getUserValidRequestReturnsOkResponseWithUserEntity() throws Exception {
        userEntity = new UserEntity("John", "Doe", "JohnD", "johndoe@example.com", "password", UserRole.USER);
        userRepository.save(userEntity);

        mockMvc.perform(get("/api/v1/user")
                        .param("email", userEntity.getEmail()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").exists())
                .andExpect(jsonPath("$.errorMessage").doesNotExist())
                .andExpect(jsonPath("$.userEntity").exists());
    }

    @Test
    void getUserThrowsNotFoundExceptionWhenNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/user")
                        .param("email", "nonexistent@example.com"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").exists())
                .andExpect(jsonPath("$.errorMessage").exists())
                .andExpect(jsonPath("$.userEntity").doesNotExist());

        userEntity = userRepository.findByEmail(userRequest.getEmail()).orElse(null);
        assertNull(userEntity);
    }

    @Test
    @SneakyThrows
    void updateUserCredentialsReturnsOkResponseAndUpdatesUserEntity() {
        userEntity = new UserEntity("John", "Doe", "JohnD", "johndoe@example.com", "password", UserRole.USER);
        userRepository.save(userEntity);
        userRequest.setDisplayName("JohnnyD");
        userRequest.setEmail("johndoe@example.com");

        mockMvc.perform(put("/api/v1/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(userRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").exists())
                .andExpect(jsonPath("$.errorMessage").doesNotExist())
                .andExpect(jsonPath("$.userEntity").exists());

        userEntity = userRepository.findByEmail(userRequest.getEmail()).orElse(null);
        assertNotNull(userEntity);
        assertEquals(userRequest.getDisplayName(), userEntity.getDisplayName());
    }

    @Test
    @SneakyThrows
    void updateUserCredentialsThrowsNotFoundExceptionWhenUserNotFound() {
        userRequest.setEmail("johndoe@example.com");

        mockMvc.perform(put("/api/v1/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(userRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").exists())
                .andExpect(jsonPath("$.errorMessage").exists())
                .andExpect(jsonPath("$.userEntity").doesNotExist());

        userEntity = userRepository.findByEmail(userRequest.getEmail()).orElse(null);
        assertNull(userEntity);
    }

    @Test
    @SneakyThrows
    void updateUserCredentialsThrowsBadRequestExceptionWhenRequestIsInvalid() {
        userEntity = new UserEntity("John", "Doe", "JohnD", "johndoe@example.com", "password", UserRole.USER);
        userRepository.save(userEntity);
        userRequest.setDisplayName(""); // invalid display name
        userRequest.setEmail("johndoe@example.com");

        mockMvc.perform(put("/api/v1/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(userRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").exists())
                .andExpect(jsonPath("$.errorMessage").exists())
                .andExpect(jsonPath("$.userEntity").doesNotExist());

        userEntity = userRepository.findByEmail(userRequest.getEmail()).orElse(null);
        assertNotNull(userEntity);
        assertNotEquals(userRequest.getDisplayName(), userEntity.getDisplayName());
    }

//    @Test
//    @SneakyThrows
//    void authenticateUserReturnsOkResponseAndAuthenticatesUserWhenRequestIsValid() {
//        // given
//        userEntity = new UserEntity("John", "Doe", "JohnD", "johndoe@example.com", bCryptPasswordEncoder.encode("P&assword123"), UserRole.USER);
//        userRepository.save(userEntity);
//        authRequest.setEmail("johndoe@example.com");
//        authRequest.setPassword("P&assword123");
//
//        // when
//        mockMvc.perform(post("/api/v1/user")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(new ObjectMapper().writeValueAsString(authRequest)))
//                .andExpect(status().isOk());
//    }
//
//    @Test
//    @SneakyThrows
//    void authenticateUserReturnsNotFoundResponseWhenUserIsNotFoundInDB() {
//        // given
//        authRequest.setEmail("johndoe@example.com");
//        authRequest.setPassword("P&assword123");
//
//        // when
//        mockMvc.perform(post("/api/v1/user")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(new ObjectMapper().writeValueAsString(authRequest)))
//                .andExpect(status().isNotFound());
//    }
//
//    @Test
//    @SneakyThrows
//    void authenticateUserReturnsUnauthorizedResponseWhenRequestPasswordIsIncorrect() {
//        // given
//        userEntity = new UserEntity("John", "Doe", "JohnD", "johndoe@example.com", bCryptPasswordEncoder.encode("P&assword123"), UserRole.USER);
//        userRepository.save(userEntity);
//        authRequest.setEmail("johndoe@example.com");
//        authRequest.setPassword("P&assword123");
//
//        // when
//        mockMvc.perform(post("/api/v1/user")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(new ObjectMapper().writeValueAsString(authRequest)))
//                .andExpect(status().isUnauthorized());
//    }
}

