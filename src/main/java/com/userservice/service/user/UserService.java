package com.userservice.service.user;

import com.userservice.exception.NotFoundException;
import com.userservice.service.response.Response;
import com.userservice.service.response.ResponseBuilder;
import com.userservice.validator.RequestValidator;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolationException;

@Service
@AllArgsConstructor
@Slf4j
public class UserService {
    private UserRepository userRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private ResponseBuilder responseBuilder;
    private UserEntity userEntity;

    public ResponseEntity<Response> updateUserCredentials(UserRequest request) throws NotFoundException {
        try {
            RequestValidator.validateRequest(request);
            userEntity = userRepository.findByEmail(request.getEmail()).orElseThrow(() ->
                    new NotFoundException("No user exists in DB with given credentials. Correlation Id: " + MDC.get("x-correlation-id")));
        }
        catch (ConstraintViolationException ex) {
            log.error(ex.getClass().getSimpleName() + " raised. Correlation Id: " + MDC.get("x-correlation-id"));
            return responseBuilder.buildResponse(ex, null);
        }
        catch (NotFoundException ex) {
            return responseBuilder.buildResponse(ex, null);
        }


        if (request.getPassword() != null) {
            userEntity.setPassword(bCryptPasswordEncoder.encode(request.getPassword()));
        }
        if (request.getDisplayName() != null) {
            userEntity.setDisplayName(request.getDisplayName());
        }
        if (request.getNewEmail() != null) {
            userEntity.setEmail(request.getNewEmail());
        }
        if (request.getFirstName() != null) {
            userEntity.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            userEntity.setLastName(request.getLastName());
        }
        if (request.getLocked()) {
            userEntity.setLocked(true);
        }
        userRepository.save(userEntity);

        return responseBuilder.buildResponse(null, userEntity);
    }
}
