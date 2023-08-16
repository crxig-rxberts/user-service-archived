package com.userservice.service.registration.token;

import com.userservice.exception.NotFoundException;
import com.userservice.exception.ConflictException;
import com.userservice.model.entity.ConfirmationTokenEntity;
import com.userservice.model.entity.UserEntity;
import com.userservice.repository.ConfirmationTokenRepository;
import com.userservice.repository.UserRepository;
import com.userservice.model.entity.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConfirmationTokenServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ConfirmationTokenRepository confirmationTokenRepository;

    @InjectMocks
    private ConfirmationTokenService confirmationTokenService;

    private String token;
    private ConfirmationTokenEntity tokenEntity;

    @BeforeEach
    void setUp() {
        token = "test-token";
        UserEntity userEntity = new UserEntity("John", "Doe", "JohnDoe", "johndoe@example.com", "password", UserRole.USER);
        tokenEntity = new ConfirmationTokenEntity(token, LocalDateTime.now(), LocalDateTime.now().plusMinutes(30), userEntity);
    }

    @Test
    void confirmToken_when_tokenIsValid_then_confirmToken() {
        when(confirmationTokenRepository.findByToken(token)).thenReturn(Optional.of(tokenEntity));

        confirmationTokenService.confirmToken(token);

        verify(userRepository, never()).delete(any());
        verify(confirmationTokenRepository, times(1)).delete(tokenEntity);
    }

    @Test
    void confirmToken_when_tokenNotFound_then_throwNotFoundException() {
        when(confirmationTokenRepository.findByToken(token)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> confirmationTokenService.confirmToken(token));
    }

    @Test
    void confirmToken_when_tokenAlreadyConfirmed_then_throwConflictException() {
        tokenEntity.setConfirmedAt(LocalDateTime.now());
        when(confirmationTokenRepository.findByToken(token)).thenReturn(Optional.of(tokenEntity));

        assertThrows(ConflictException.class, () -> confirmationTokenService.confirmToken(token));
    }

    @Test
    void confirmToken_when_tokenExpired_then_throwConflictExceptionAndDeleteUser() {
        tokenEntity.setExpiresAt(LocalDateTime.now().minusMinutes(1));
        when(confirmationTokenRepository.findByToken(token)).thenReturn(Optional.of(tokenEntity));

        assertThrows(ConflictException.class, () -> confirmationTokenService.confirmToken(token));
        verify(userRepository, times(1)).delete(tokenEntity.getUserEntity());
    }
}

