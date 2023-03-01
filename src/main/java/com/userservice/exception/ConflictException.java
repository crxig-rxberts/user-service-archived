package com.userservice.exception;

import lombok.Getter;

@Getter
public class ConflictException extends ServiceException {

    /**
     * Instantiates new UserEntityNotFoundException
     *
     * @param msg the msg
     */
    public ConflictException(String msg) {
        super(msg);
    }
}
