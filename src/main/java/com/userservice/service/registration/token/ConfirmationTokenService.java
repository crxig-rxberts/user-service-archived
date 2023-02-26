package com.userservice.service.registration.token;

import com.userservice.exception.NotFoundException;
import com.userservice.exception.ConflictException;
import com.userservice.service.user.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.transaction.Transactional;
import java.time.LocalDateTime;

@Service
@AllArgsConstructor
@Slf4j
public class ConfirmationTokenService {

    private UserRepository userRepository;

    private final ConfirmationTokenRepository confirmationTokenRepository;

    @Transactional
    @ResponseStatus
    public void confirmToken(String token) throws NotFoundException {

        ConfirmationToken tokenEntity = confirmationTokenRepository.findByToken(token)
                .orElseThrow(() -> new NotFoundException("Token not found. Correlation Id: " + MDC.get("x-correlation-id")));

        if (tokenEntity.getConfirmedAt() != null) {
            throw new ConflictException("Token already confirmed. Correlation Id: " + MDC.get("x-correlation-id"));
        }

        if (tokenEntity.getExpiresAt().isBefore(LocalDateTime.now())) {
            userRepository.delete(tokenEntity.getUserEntity());
            throw new ConflictException("Token Expired. Correlation Id: " + MDC.get("x-correlation-id"));
        }

        tokenEntity.setConfirmedAt(LocalDateTime.now());
        tokenEntity.getUserEntity().setEnabled(true);
        confirmationTokenRepository.delete(tokenEntity);
        log.info("User account enabled. Token Entity has been deleted from DB. " + "Correlation Id: " + MDC.get("x-correlation-id"));
    }
}
