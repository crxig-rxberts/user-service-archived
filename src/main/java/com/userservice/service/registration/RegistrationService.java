package com.userservice.service.registration;

import com.userservice.exception.ConflictException;
import com.userservice.service.response.Response;
import com.userservice.service.registration.email.EmailBuilder;
import com.userservice.service.registration.email.EmailSender;
import com.userservice.service.registration.token.ConfirmationToken;
import com.userservice.service.registration.token.ConfirmationTokenRepository;
import com.userservice.service.response.ResponseBuilder;
import com.userservice.service.user.UserEntity;
import com.userservice.service.user.UserRepository;
import com.userservice.service.user.UserRole;
import com.userservice.validator.RequestValidator;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@AllArgsConstructor
@Slf4j
public class RegistrationService implements UserDetailsService {
    private final ConfirmationTokenRepository confirmationTokenRepository;
    private final EmailSender emailSender;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final ResponseBuilder responseBuilder;


    public ResponseEntity<Response> register(RegistrationRequest request) {
        try {
            RequestValidator.validateRequest(request);
            userRepository.findByEmail(request.getEmail()).ifPresent(userEntity -> {
                        throw new ConflictException(String.format("User already exists in DB. CorrelationId: %s", MDC.get("x-correlation-id")));
                    });
        }
        catch (ConstraintViolationException ex) {
            log.warn(ex.getClass().getSimpleName() + " raised. Correlation Id: " + MDC.get("x-correlation-id"));
            return responseBuilder.buildResponse(ex);
        }
        catch (ConflictException ex) {
            return responseBuilder.buildResponse(ex);
        }

        ConfirmationToken confirmationToken = signUpUser(
                new UserEntity(
                        request.getFirstName(),
                        request.getLastName(),
                        request.getDisplayName(),
                        request.getEmail(),
                        bCryptPasswordEncoder.encode(request.getPassword()),
                        UserRole.USER
                )
        );
        String confirmationLink = "https://localhost:8080/api/v1/registration/confirm?token=" + confirmationToken.getToken();
        emailSender.send(request.getEmail(), EmailBuilder.buildEmail(request.getFirstName(), confirmationLink));

        return responseBuilder.buildResponse(confirmationToken.getUserEntity());
    }



    public ConfirmationToken signUpUser(UserEntity userEntity) {

        userRepository.save(userEntity);
        log.info("User successfully saved to DB. Correlation Id: " + MDC.get("x-correlation-id"));

        ConfirmationToken confirmationToken = new ConfirmationToken(
                UUID.randomUUID().toString(),
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(30),
                userEntity
        );
        confirmationTokenRepository.save(confirmationToken);
        log.info(String.format("Confirmation token successfully saved to DB. Token: %s Correlation Id: %s", confirmationToken.getToken(), MDC.get("x-correlation-id")));

        return confirmationToken;
    }

    @Override
    public UserDetails loadUserByUsername(String displayName) throws UsernameNotFoundException {
        return userRepository.findByDisplayName(displayName);
    }
}
