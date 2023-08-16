package com.userservice.service.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.userservice.model.request.LoginRequest;
import com.userservice.model.entity.UserEntity;
import com.userservice.model.request.UserRequest;
import com.userservice.model.entity.UserRole;
import com.userservice.repository.UserRepository;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

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
@Transactional
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    private UserEntity userEntity;

    @Test
    void getUserValidRequestReturnsOkResponseWithUserEntity() throws Exception {
        createUserForAuthorisation();

        mockMvc.perform(get("/api/auth/user")
                        .header("Authorization", obtainAccessToken())
                        .param("email", "johndoe@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.userEntity").exists());
    }

    @Test
    void getUserThrowsNotFoundExceptionWhenNotFound() throws Exception {
        createUserForAuthorisation();

        mockMvc.perform(get("/api/auth/user")
                        .header("Authorization", obtainAccessToken())
                        .param("email", "nonexistent@example.com"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("NOT_FOUND"))
                .andExpect(jsonPath("$.userEntity").doesNotExist());

        userEntity = userRepository.findUserByEmail("nonexistent@example.com").orElse(null);
        assertNull(userEntity);
    }


    @Test
    @SneakyThrows
    void updateUserCredentialsReturnsOkResponseAndUpdatesUserEntity() {
        createUserForAuthorisation();
        UserRequest userRequest = UserRequest.builder()
                .displayName("updatedDisplayName")
                .email("johndoe@example.com")
                .password("P$ssW0rd123")
                .build();

        mockMvc.perform(put("/api/auth/update")
                        .header("Authorization", obtainAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(userRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").exists())
                .andExpect(jsonPath("$.errorMessage").doesNotExist())
                .andExpect(jsonPath("$.userEntity").exists());

        userEntity = userRepository.findUserByEmail(userRequest.getEmail()).orElse(null);
        assertNotNull(userEntity);
        assertEquals(userRequest.getDisplayName(), userEntity.getDisplayName());
    }


    @Test
    @SneakyThrows
    void updateUserCredentialsThrowsNotFoundExceptionWhenUserNotFound() {
        createUserForAuthorisation();
        UserRequest userRequest = UserRequest.builder()
                .email("nonexistent@example.com")
                .build();

        mockMvc.perform(put("/api/auth/update")
                        .header("Authorization", obtainAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(userRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("NOT_FOUND"))
                .andExpect(jsonPath("$.userEntity").doesNotExist());

        userEntity = userRepository.findUserByEmail(userRequest.getEmail()).orElse(null);
        assertNull(userEntity);
    }

    @Test
    @SneakyThrows
    void updateUserCredentialsThrowsBadRequestExceptionWhenRequestIsInvalid() {
        createUserForAuthorisation();
        UserRequest userRequest = UserRequest.builder()
                .email("johndoe@example.com")
                .displayName("")
                .build();

        mockMvc.perform(put("/api/auth/update")
                        .header("Authorization", obtainAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(userRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.errorMessage").exists())
                .andExpect(jsonPath("$.userEntity").doesNotExist());

        userEntity = userRepository.findUserByEmail(userRequest.getEmail()).orElse(null);
        assertNotNull(userEntity);
        assertNotEquals(userRequest.getDisplayName(), userEntity.getDisplayName());
    }

    @Test
    @SneakyThrows
    void loginReturnsOkResponseAndAuthenticatesUserWhenRequestIsValid() {
        createUserForAuthorisation();
        LoginRequest loginRequest = LoginRequest.builder()
                .email("johndoe@example.com")
                .password("P$ssW0rd123")
                .build();

        mockMvc.perform(post("/api/public/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.jwtToken").isNotEmpty());
    }

    @Test
    @SneakyThrows
    void loginReturnsNotFoundResponseWhenUserIsNotFoundInDB() {
        LoginRequest loginRequest = LoginRequest.builder()
                .email("nonexistent@example.com")
                .password("P$ssW0rd123")
                .build();

        // when
        mockMvc.perform(post("/api/public/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(loginRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("NOT_FOUND"));

    }

    @Test
    @SneakyThrows
    void loginReturnsUnauthorizedResponseWhenRequestPasswordIsIncorrect() {

        createUserForAuthorisation();
        LoginRequest loginRequest = LoginRequest.builder()
                .email("johndoe@example.com")
                .password("Password")
                .build();

        // when
        mockMvc.perform(post("/api/public/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value("UNAUTHORIZED"))

                .andExpect(jsonPath("$.errorMessage").exists());

    }

    private String obtainAccessToken() throws Exception {
        LoginRequest loginRequest = new LoginRequest("johndoe@example.com", "P$ssW0rd123");
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonLoginRequest = objectMapper.writeValueAsString(loginRequest);

        MvcResult result = mockMvc.perform(post("/api/public/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonLoginRequest))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();

        return "Bearer " + JsonPath.read(responseBody, "$.jwtToken");
    }

    private void createUserForAuthorisation() {
        userEntity = new UserEntity("John", "Doe", "JohnD", "johndoe@example.com", bCryptPasswordEncoder.encode("P$ssW0rd123"), UserRole.USER);
        userRepository.save(userEntity);
    }

}

