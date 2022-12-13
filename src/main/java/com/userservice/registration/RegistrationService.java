package com.userservice.registration;

import com.userservice.email.EmailBuilder;
import com.userservice.email.EmailValidator;
import com.userservice.user.AppUser;
import com.userservice.user.AppUserRole;
import com.userservice.user.AppUserService;
import com.userservice.email.EmailSender;
import com.userservice.registration.token.ConfirmationToken;
import com.userservice.registration.token.ConfirmationTokenService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class RegistrationService {
    private final EmailValidator emailValidator;
    private final AppUserService appUserService;
    private final ConfirmationTokenService confirmationTokenService;
    private final EmailSender emailSender;


    public String register(RegistrationRequest request) {
        boolean isValidEmail = emailValidator.test(request.getEmail());
        if (!isValidEmail) {
            throw new IllegalStateException("Email not valid");
        }

        String token = appUserService.signUpUser(
                new AppUser(
                        request.getFirstName(),
                        request.getLastName(),
                        request.getDisplayName(),
                        request.getEmail(),
                        request.getPassword(),
                        AppUserRole.USER
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
                        new IllegalStateException("Token not found"));

        if (confirmationToken.getConfirmedAt() != null) {
            throw new IllegalStateException("Email already confirmed");
        }

        LocalDateTime expiredAt = confirmationToken.getExpiresAt();

        if (expiredAt.isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Token expired, please re-register");
        }

        confirmationTokenService.setConfirmedAt(token);
        appUserService.enableAppUser(
                confirmationToken.getAppUser().getEmail());
        return "Confirmed";
    }



}
