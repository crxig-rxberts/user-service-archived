package com.userservice.exception;

import lombok.Getter;

@Getter
public class UnauthorizedException extends ServiceException {

    /**
     * Instantiates new UserEntityNotFoundException
     *
     * @param msg the msg
     */
    public UnauthorizedException(String msg) {
        super(msg);
    }
}