package com.userservice.registration;

import com.userservice.registration.email.EmailBuilder;
import com.userservice.registration.email.EmailValidator;
import com.userservice.user.*;
import com.userservice.registration.email.EmailSender;
import com.userservice.registration.token.ConfirmationToken;
import com.userservice.registration.token.ConfirmationTokenService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@AllArgsConstructor
@Slf4j
public class RegistrationService implements UserDetailsService {
    private final EmailValidator emailValidator;
    private final ConfirmationTokenService confirmationTokenService;
    private final EmailSender emailSender;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;


    public String register(RegistrationRequest request) {
        boolean isValidEmail = emailValidator.test(request.getEmail());
        if (!isValidEmail) {
            throw new IllegalStateException("Email not valid");
        }

        String token = signUpUser(
                new UserEntity(
                        request.getFirstName(),
                        request.getLastName(),
                        request.getDisplayName(),
                        request.getEmail(),
                        request.getPassword(),
                        UserRole.USER
                )
        );
        String link = "https://localhost:8080/api/v1/registration/confirm?token=" + token;
        emailSender.send(request.getEmail(), EmailBuilder.buildEmail(request.getFirstName(), link));

        return token;
    }


    @Transactional
    public String confirmToken(String token) {
        ConfirmationToken confirmationToken = confirmationTokenService
                .getToken(token)
                .orElseThrow(() ->
                        new IllegalStateException("Token not found."));

        if (confirmationToken.getConfirmedAt() != null) {
            throw new IllegalStateException("Email already confirmed.");
        }

        LocalDateTime expiredAt = confirmationToken.getExpiresAt();

        if (expiredAt.isBefore(LocalDateTime.now())) {
            // TODO: create method to delete user from DB.
            throw new IllegalStateException("Token expired, please re-register.");
        }

        confirmationTokenService.setConfirmedAt(token);
        log.info("User account enabled. Correlation Id: " + MDC.get("x-correlation-id"));
        enableAppUser(
                confirmationToken.getUserEntity().getEmail());

        // TODO: Send Verification Email after confirmation
        return "User email has been confirmed";
    }


    public String signUpUser(UserEntity userEntity) {

        if (userRepository.findByEmail(userEntity.getEmail()).isPresent()) {
            throw new IllegalStateException("Email already in use");
        }

        String encodedPassword = bCryptPasswordEncoder.encode(userEntity.getPassword());
        userEntity.setPassword(encodedPassword);

        userRepository.save(userEntity);
        log.info("User details successfully saved to database. Correlation Id: " + MDC.get("x-correlation-id"));

        String token = UUID.randomUUID().toString();
        ConfirmationToken confirmationToken = new ConfirmationToken(
                token,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(30),
                userEntity
        );

        confirmationTokenService.saveConfirmationToken(confirmationToken);

        return token;
    }

    public void enableAppUser(String email) {
        userRepository.enableAppUser(email);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return null;
    }
}
