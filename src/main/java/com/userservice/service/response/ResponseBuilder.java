package com.userservice.service.response;

import com.userservice.service.user.UserEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Slf4j

@Component
public class ResponseBuilder {

    private final String CONFLICT_MSG = "User already exists in DB.";
    private final String NOT_FOUND_MSG = "No entity exists in DB with given credentials.";
    public ResponseEntity<Response> buildResponse(RuntimeException exception) {
        Response response = new Response();

        switch (exception.getClass().getSimpleName()) {
            case "ConflictException" -> {
                response.setStatus(ResponseStatus.CONFLICT);
                response.setErrorMessage(CONFLICT_MSG);
                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
            }
            case "NotFoundException" -> {
                response.setStatus(ResponseStatus.NOT_FOUND);
                response.setErrorMessage(NOT_FOUND_MSG);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            case "ConstraintViolationException" -> {
                response.setStatus(ResponseStatus.BAD_REQUEST);
                response.setErrorMessage(exception.getMessage());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
            default -> {
                response.setStatus(ResponseStatus.INTERNAL_ERROR);
                response.setErrorMessage(exception.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
        }
    }

    public ResponseEntity<Response> buildResponse(UserEntity userEntity) {
        Response response = new Response();

        response.setStatus(ResponseStatus.SUCCESS);
        response.setUserEntity(userEntity);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}

