package com.userservice.service.response;

import com.userservice.exception.ConflictException;
import com.userservice.exception.NotFoundException;
import com.userservice.service.user.UserEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintViolationException;


@Component
public class ResponseBuilder {

    private final String CONFLICT_MSG = "User already exists in DB.";
    private final String NOT_FOUND_MSG = "No entity exists in DB with given credentials.";
    public ResponseEntity<Response> buildResponse(RuntimeException ex, UserEntity userEntity) {

        Response response = new Response();

        if (ex != null) {

            if (ConflictException.class.equals(ex.getClass())) {
                response.setStatus(ResponseStatus.CONFLICT);
                response.setErrorMessage(CONFLICT_MSG);
                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);

            } else if (NotFoundException.class.equals(ex.getClass())) {
                response.setStatus(ResponseStatus.NOT_FOUND);
                response.setErrorMessage(NOT_FOUND_MSG);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);

            } else if (ConstraintViolationException.class.equals(ex.getClass())) {
                response.setStatus(ResponseStatus.BAD_REQUEST);
                response.setErrorMessage(ex.getMessage());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
            response.setStatus(ResponseStatus.INTERNAL_ERROR);
            response.setErrorMessage(ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
        else {
            response.setUserEntity(userEntity);
            response.setStatus(ResponseStatus.SUCCESS);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }
    }
}
