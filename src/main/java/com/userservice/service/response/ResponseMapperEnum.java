package com.userservice.service.response;

import com.userservice.service.user.UserEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public enum ResponseMapperEnum {
    CONFLICT {
        @Override
        public ResponseEntity<Response> buildResponse(RuntimeException exception) {
            return buildErrorResponse(exception, ResponseStatus.CONFLICT, HttpStatus.OK);
        }
    },
    NOT_FOUND_EXCEPTION {
        @Override
        public ResponseEntity<Response> buildResponse(RuntimeException exception) {
            return buildErrorResponse(exception, ResponseStatus.NOT_FOUND, HttpStatus.OK);
        }
    },
    CONSTRAINT_VIOLATION {
        @Override
        public ResponseEntity<Response> buildResponse(RuntimeException exception) {
            return buildErrorResponse(exception, ResponseStatus.BAD_REQUEST, HttpStatus.OK);
        }
    },
    INTERNAL_SERVER_ERROR {
        @Override
        public ResponseEntity<Response> buildResponse(RuntimeException exception) {
            return buildErrorResponse(exception, ResponseStatus.INTERNAL_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    };

    public abstract ResponseEntity<Response> buildResponse(RuntimeException exception);

    protected ResponseEntity<Response> buildErrorResponse(RuntimeException exception, ResponseStatus responseStatus, HttpStatus status) {
        Response response = new Response();
        response.setStatus(responseStatus);
        response.setErrorMessage(exception.getMessage());
        return ResponseEntity.status(status).body(response);
    }

    public static ResponseEntity<Response> mapExceptionToResponse(RuntimeException exception) {
        for (ResponseMapperEnum mapper : values()) {
            if (mapper.name().equalsIgnoreCase(exception.getClass().getSimpleName())) {
                return mapper.buildResponse(exception);
            }
        }
        return INTERNAL_SERVER_ERROR.buildResponse(exception);
    }

    public static ResponseEntity<Response> buildSuccessResponse(UserEntity userEntity) {
        Response response = new Response();
        response.setStatus(ResponseStatus.SUCCESS);
        response.setUserEntity(userEntity);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}