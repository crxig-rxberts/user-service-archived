package com.userservice.registration.token;

import com.userservice.user.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class ConfirmationTokenService {

    private UserRepository userRepository;

    private final ConfirmationTokenRepository confirmationTokenRepository;

    public void saveConfirmationToken(ConfirmationToken token) {
        confirmationTokenRepository.save(token);
    }

    public Optional<ConfirmationToken> getToken(String token) {
        return confirmationTokenRepository.findByToken(token);
    }

    @Transactional
    @ResponseStatus
    public ResponseEntity<String> confirmToken(String token) {
        ConfirmationToken tokenEntity = getToken(token)
                .orElseThrow(() -> new IllegalStateException("Token not found."));

        if (tokenEntity.getConfirmedAt() != null) {
            throw new IllegalStateException("Email already confirmed.");
        }

        LocalDateTime expiredAt = tokenEntity.getExpiresAt();

        if (expiredAt.isBefore(LocalDateTime.now())) {
            // TODO: create method to delete user from DB.
            throw new IllegalStateException("Token expired, please re-register.");
        }

        tokenEntity.setConfirmedAt(LocalDateTime.now());
        enableAppUser(tokenEntity.getUserEntity().getEmail());
        log.info("User account enabled. Correlation Id: " + MDC.get("x-correlation-id"));

        // TODO: Send Verification Email after confirmation
        return ResponseEntity.status(HttpStatus.OK)
                .body(token);
    }


    public void setConfirmedAt(String token) {
        confirmationTokenRepository.updateConfirmedAt(
                token, LocalDateTime.now());
    }

    public void enableAppUser(String email) {
        userRepository.enableAppUser(email);
    }

}
