package com.userservice.exception;

public class JwtTokenInvalidException extends ServiceException {
    public JwtTokenInvalidException(String msg) { super(msg); }
}
