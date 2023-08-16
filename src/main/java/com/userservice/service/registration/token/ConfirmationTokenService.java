package com.userservice.service.registration.token;

import com.userservice.exception.NotFoundException;
import com.userservice.exception.ConflictException;
import com.userservice.model.entity.ConfirmationTokenEntity;
import com.userservice.repository.ConfirmationTokenRepository;
import com.userservice.repository.UserRepository;
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

    private static final String NOT_FOUND_ERROR_MESSAGE = "ConfirmationToken does not exist.";
    private static final String CONFIRMED_ERROR_MESSAGE = "ConfirmationToken already confirmed.";
    private static final String EXPIRED_ERROR_MESSAGE = "ConfirmationToken Expired";

    private UserRepository userRepository;
    private ConfirmationTokenRepository confirmationTokenRepository;

    @Transactional
    @ResponseStatus
    public void confirmToken(String token) throws SecurityException {

        ConfirmationTokenEntity tokenEntity = confirmationTokenRepository.findByToken(token)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_ERROR_MESSAGE));

        //TODO: Make new exceptions
        if (tokenEntity.getConfirmedAt() != null) {
            throw new ConflictException(CONFIRMED_ERROR_MESSAGE);
        }

        if (tokenEntity.getExpiresAt().isBefore(LocalDateTime.now())) {
            userRepository.delete(tokenEntity.getUserEntity());
            throw new ConflictException(EXPIRED_ERROR_MESSAGE);
        }

        tokenEntity.setConfirmedAt(LocalDateTime.now());
        tokenEntity.getUserEntity().setEnabled(true);
        confirmationTokenRepository.delete(tokenEntity);
        log.info("User account enabled. Token Entity has been deleted from DB. " + "Correlation Id: " + MDC.get("x-correlation-id"));
    }
}
