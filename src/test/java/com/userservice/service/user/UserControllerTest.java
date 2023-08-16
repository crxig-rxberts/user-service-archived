package com.userservice.service.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.userservice.model.entity.UserEntity;
import com.userservice.model.entity.UserRole;
import com.userservice.model.request.LoginRequest;
import com.userservice.model.request.UpdateDetailsRequest;
import com.userservice.model.request.UpdateEmailRequest;
import com.userservice.model.request.UpdatePasswordRequest;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("local")
@AutoConfigureMockMvc
@Transactional
class UserControllerTest {

    private static final String USER_ENDPOINT = "/api/auth/user";
    private static final String UPDATE_PASSWORD_ENDPOINT = "/api/auth/update/password";
    private static final String UPDATE_EMAIL_ENDPOINT = "/api/auth/update/email";
    private static final String UPDATE_DETAILS_ENDPOINT = "/api/auth/update/details";
    private static final String VALID_EMAIL = "johndoe@example.com";
    private static final String VALID_PASSWORD = "P$ssW0rd123";
    private static final String NEW_PASSWORD = "NewP$ssW0rd123";
    private static final String NEW_EMAIL = "newjohndoe@example.com";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @SneakyThrows
    void testGetUser() {
        createUser();
        mockMvc.perform(get(USER_ENDPOINT).param("email", VALID_EMAIL)
                        .header("Authorization", obtainAccessToken()))
                .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    void testUpdatePassword() {
        createUser();
        UpdatePasswordRequest request = new UpdatePasswordRequest(VALID_EMAIL, VALID_PASSWORD, NEW_PASSWORD);

        mockMvc.perform(put(UPDATE_PASSWORD_ENDPOINT)
                        .header("Authorization", obtainAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    void testUpdateEmail() {
        createUser();
        UpdateEmailRequest request = new UpdateEmailRequest(VALID_EMAIL, VALID_PASSWORD, NEW_EMAIL);

        mockMvc.perform(put(UPDATE_EMAIL_ENDPOINT)
                        .header("Authorization", obtainAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    void testUpdateDetails() {
        createUser();
        UpdateDetailsRequest request = new UpdateDetailsRequest(VALID_EMAIL, VALID_PASSWORD, "NewDisplayName", "NewFirst", "NewLast");

        mockMvc.perform(put(UPDATE_DETAILS_ENDPOINT)
                        .header("Authorization", obtainAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    private String obtainAccessToken() throws Exception {
        LoginRequest loginRequest = new LoginRequest(VALID_EMAIL, VALID_PASSWORD);
        String jsonLoginRequest = objectMapper.writeValueAsString(loginRequest);

        MvcResult result = mockMvc.perform(post("/api/public/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonLoginRequest))
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();

        return "Bearer " + JsonPath.read(responseBody, "$.jwtToken");
    }

    @Test
    @SneakyThrows
    void testGetUser_NotFound() {
        createUser();
        mockMvc.perform(get(USER_ENDPOINT).param("email", NEW_EMAIL)
                        .header("Authorization", obtainAccessToken()))
                .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    void testUpdatePassword_UserNotFound() {
        createUser();
        UpdatePasswordRequest request = new UpdatePasswordRequest(NEW_EMAIL, VALID_PASSWORD, NEW_PASSWORD);
        mockMvc.perform(put(UPDATE_PASSWORD_ENDPOINT)
                        .header("Authorization", obtainAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    void testUpdatePassword_IncorrectPassword() {
        createUser();
        UpdatePasswordRequest request = new UpdatePasswordRequest(VALID_EMAIL, "WrongPassword", NEW_PASSWORD);
        mockMvc.perform(put(UPDATE_PASSWORD_ENDPOINT)
                        .header("Authorization", obtainAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @SneakyThrows
    void testUpdateEmail_UserNotFound() {
        createUser();
        UpdateEmailRequest request = new UpdateEmailRequest(NEW_EMAIL, VALID_PASSWORD, NEW_EMAIL);
        mockMvc.perform(put(UPDATE_EMAIL_ENDPOINT)
                        .header("Authorization", obtainAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    void testUpdateEmail_IncorrectPassword() {
        createUser();
        UpdateEmailRequest request = new UpdateEmailRequest(VALID_EMAIL, "WrongPassword", NEW_EMAIL);
        mockMvc.perform(put(UPDATE_EMAIL_ENDPOINT)
                        .header("Authorization", obtainAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @SneakyThrows
    void testUpdateDetails_UserNotFound() {
        createUser();
        UpdateDetailsRequest request = new UpdateDetailsRequest(NEW_EMAIL, VALID_PASSWORD, "NewDisplayName", "NewFirst", "NewLast");
        mockMvc.perform(put(UPDATE_DETAILS_ENDPOINT)
                        .header("Authorization", obtainAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    void testUpdateDetails_IncorrectPassword() {
        createUser();
        UpdateDetailsRequest request = new UpdateDetailsRequest(VALID_EMAIL, "WrongPassword", "NewDisplayName", "NewFirst", "NewLast");
        mockMvc.perform(put(UPDATE_DETAILS_ENDPOINT)
                        .header("Authorization", obtainAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    private void createUser() {
        UserEntity userEntity = new UserEntity("John", "Doe", "JohnD", VALID_EMAIL, bCryptPasswordEncoder.encode(VALID_PASSWORD), UserRole.USER);
        userRepository.save(userEntity);
    }
}


