package com.userservice.model.response.mapper;

import com.userservice.model.entity.UserEntity;
import com.userservice.model.response.BaseResponse;
import com.userservice.model.response.UserResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public enum ResponseMapperEnum {
    CONFLICT {
        @Override
        public ResponseEntity<BaseResponse> buildResponse(RuntimeException exception) {
            return buildErrorResponse(exception, ResponseStatus.CONFLICT, HttpStatus.OK);
        }
    },
    NOT_FOUND_EXCEPTION {
        @Override
        public ResponseEntity<BaseResponse> buildResponse(RuntimeException exception) {
            return buildErrorResponse(exception, ResponseStatus.NOT_FOUND, HttpStatus.OK);
        }
    },
    CONSTRAINT_VIOLATION {
        @Override
        public ResponseEntity<BaseResponse> buildResponse(RuntimeException exception) {
            return buildErrorResponse(exception, ResponseStatus.BAD_REQUEST, HttpStatus.OK);
        }
    },
    INTERNAL_SERVER_ERROR {
        @Override
        public ResponseEntity<BaseResponse> buildResponse(RuntimeException exception) {
            return buildErrorResponse(exception, ResponseStatus.INTERNAL_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    };

    public abstract ResponseEntity<BaseResponse> buildResponse(RuntimeException exception);

    protected ResponseEntity<BaseResponse> buildErrorResponse(RuntimeException exception, ResponseStatus responseStatus, HttpStatus status) {
        BaseResponse response = new BaseResponse();
        response.setStatus(responseStatus);
        response.setErrorMessage(exception.getMessage());
        return ResponseEntity.status(status).body(response);
    }

    public static ResponseEntity<BaseResponse> mapExceptionToResponse(RuntimeException exception) {
        for (ResponseMapperEnum mapper : values()) {
            if (mapper.name().equalsIgnoreCase(exception.getClass().getSimpleName())) {
                return mapper.buildResponse(exception);
            }
        }
        return INTERNAL_SERVER_ERROR.buildResponse(exception);
    }

    public static ResponseEntity<UserResponse> buildSuccessResponse(UserEntity userEntity) {
        UserResponse response = new UserResponse();
        response.setStatus(ResponseStatus.SUCCESS);
        response.setUserEntity(userEntity);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}