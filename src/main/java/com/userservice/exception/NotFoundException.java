package com.userservice.exception;

import lombok.Getter;

@Getter
public class NotFoundException extends ServiceException {

    /**
     * Instantiates new UserEntityNotFoundException
     *
     * @param msg the msg
     */
    public NotFoundException(String msg) {
        super(msg);
    }
}
