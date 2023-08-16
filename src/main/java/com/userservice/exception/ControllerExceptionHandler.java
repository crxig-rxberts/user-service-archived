package com.userservice.exception;

import com.userservice.model.response.BaseResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.validation.ConstraintViolationException;

@Slf4j
@ControllerAdvice
public class ControllerExceptionHandler {

    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotFoundException.class)
    public final ResponseEntity<BaseResponse> handleNotFoundException(NotFoundException ex) {
        BaseResponse response = new BaseResponse();
        response.setStatus(com.userservice.model.response.mapper.ResponseStatus.NOT_FOUND);
        response.setErrorMessage(ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ResponseStatus(value = HttpStatus.CONFLICT)
    @ExceptionHandler(ConflictException.class)
    public final ResponseEntity<BaseResponse> handleConflictException(ConflictException ex) {
        BaseResponse response = new BaseResponse();
        response.setStatus(com.userservice.model.response.mapper.ResponseStatus.CONFLICT);
        response.setErrorMessage(ex.getLogMsg());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ResponseStatus(value = HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(UnauthorizedException.class)
    public final ResponseEntity<BaseResponse> handleUnauthorizedException(UnauthorizedException ex) {
        BaseResponse response = new BaseResponse();
        response.setStatus(com.userservice.model.response.mapper.ResponseStatus.UNAUTHORIZED);
        response.setErrorMessage(ex.getLogMsg());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ResponseStatus(value = HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(JwtTokenInvalidException.class)
    public final ResponseEntity<BaseResponse> handleJwtTokenInvalidException(JwtTokenInvalidException ex) {
        BaseResponse response = new BaseResponse();
        response.setStatus(com.userservice.model.response.mapper.ResponseStatus.UNAUTHORIZED);
        response.setErrorMessage(ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ResponseStatus(value = HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(JwtTokenMissingException.class)
    public final ResponseEntity<BaseResponse> handleJwtTokenMissingException(JwtTokenMissingException ex) {
        BaseResponse response = new BaseResponse();
        response.setStatus(com.userservice.model.response.mapper.ResponseStatus.UNAUTHORIZED);
        response.setErrorMessage(ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ConstraintViolationException.class)
    public final ResponseEntity<BaseResponse> handleJwtTokenMissingException(ConstraintViolationException ex) {
        BaseResponse response = new BaseResponse();
        response.setStatus(com.userservice.model.response.mapper.ResponseStatus.BAD_REQUEST);
        response.setErrorMessage(ex.getConstraintViolations().toString());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

}