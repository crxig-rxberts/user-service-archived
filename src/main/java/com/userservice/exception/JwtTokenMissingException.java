package com.userservice.exception;

public class JwtTokenMissingException extends ServiceException {
    public JwtTokenMissingException(String msg) { super(msg);}
}
