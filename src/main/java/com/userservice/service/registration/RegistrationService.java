package com.userservice.service.registration;

import com.userservice.exception.ConflictException;
import com.userservice.model.request.RegistrationRequest;
import com.userservice.model.response.UserResponse;
import com.userservice.model.entity.ConfirmationTokenEntity;
import com.userservice.service.registration.email.EmailBuilder;
import com.userservice.service.registration.email.EmailSender;
import com.userservice.repository.ConfirmationTokenRepository;
import com.userservice.model.response.mapper.ResponseMapper;
import com.userservice.model.entity.UserEntity;
import com.userservice.repository.UserRepository;
import com.userservice.model.entity.UserRole;
import com.userservice.validator.RequestValidator;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@AllArgsConstructor
@Slf4j
public class RegistrationService {
    private final ConfirmationTokenRepository confirmationTokenRepository;
    private final EmailSender emailSender;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final ResponseMapper responseMapper;

    public ResponseEntity<UserResponse> register(RegistrationRequest request) {

        RequestValidator.validateRequest(request);

        userRepository.findUserByEmail(request.getEmail()).ifPresent(userEntity -> {
            throw new ConflictException("User already exists in DB.");
        });
        ConfirmationTokenEntity confirmationToken = signUpUser(
                new UserEntity(
                        request.getFirstName(),
                        request.getLastName(),
                        request.getDisplayName(),
                        request.getEmail(),
                        bCryptPasswordEncoder.encode(request.getPassword()),
                        UserRole.USER
                )
        );
        String confirmationLink = "https://localhost:8080/api/registration/confirm?token=" + confirmationToken.getToken();
        emailSender.send(request.getEmail(), EmailBuilder.buildEmail(request.getFirstName(), confirmationLink));

        return responseMapper.buildUserResponse(confirmationToken.getUserEntity());
    }

    public ConfirmationTokenEntity signUpUser(UserEntity userEntity) {

        userRepository.save(userEntity);
        log.info("User successfully saved to DB.");

        ConfirmationTokenEntity confirmationToken = new ConfirmationTokenEntity(
                UUID.randomUUID().toString(),
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(30),
                userEntity
        );
        confirmationTokenRepository.save(confirmationToken);
        log.info(String.format("Confirmation token successfully saved to DB. Token: %s ", confirmationToken.getToken()));

        return confirmationToken;
    }
}
