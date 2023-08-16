package com.userservice.service.authentication;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.userservice.model.entity.UserEntity;
import com.userservice.model.entity.UserRole;
import com.userservice.model.request.LoginRequest;
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
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("local")
@AutoConfigureMockMvc
@Transactional
class AuthenticationControllerTest {

    private static final String VALID_EMAIL = "johndoe@example.com";
    private static final String NON_EXISTENT_EMAIL = "nonexistent@example.com";
    private static final String VALID_PASSWORD = "P$ssW0rd123";
    private static final String INCORRECT_PASSWORD = "Password";
    private static final String LOGIN_ENDPOINT = "/api/public/login";
    private static final String STATUS_KEY = "$.status";
    private static final String JWT_TOKEN_KEY = "$.jwtToken";
    private static final String ERROR_MESSAGE_KEY = "$.errorMessage";
    private static final String NOT_FOUND = "NOT_FOUND";
    private static final String UNAUTHORIZED = "UNAUTHORIZED";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @SneakyThrows
    void loginReturnsOkResponseAndAuthenticatesUserWhenRequestIsValid() {
        createUserForAuthorisation();
        LoginRequest loginRequest = createLoginRequest(VALID_EMAIL, VALID_PASSWORD);

        mockMvc.perform(post(LOGIN_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath(JWT_TOKEN_KEY).isNotEmpty());
    }

    @Test
    @SneakyThrows
    void loginReturnsNotFoundResponseWhenUserIsNotFoundInDB() {
        LoginRequest loginRequest = createLoginRequest(NON_EXISTENT_EMAIL, VALID_PASSWORD);

        mockMvc.perform(post(LOGIN_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath(STATUS_KEY).value(NOT_FOUND));
    }

    @Test
    @SneakyThrows
    void loginReturnsUnauthorizedResponseWhenRequestPasswordIsIncorrect() {
        createUserForAuthorisation();
        LoginRequest loginRequest = createLoginRequest(VALID_EMAIL, INCORRECT_PASSWORD);

        mockMvc.perform(post(LOGIN_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath(STATUS_KEY).value(UNAUTHORIZED))
                .andExpect(jsonPath(ERROR_MESSAGE_KEY).exists());
    }

    private void createUserForAuthorisation() {
        UserEntity userEntity = new UserEntity("John", "Doe", "JohnD", VALID_EMAIL, bCryptPasswordEncoder.encode(VALID_PASSWORD), UserRole.USER);
        userRepository.save(userEntity);
    }

    private LoginRequest createLoginRequest(String email, String password) {
        return LoginRequest.builder()
                .email(email)
                .password(password)
                .build();
    }
}
