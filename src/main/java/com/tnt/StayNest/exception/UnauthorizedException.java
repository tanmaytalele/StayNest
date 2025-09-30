package com.tnt.StayNest.exception;

public class UnauthorizedException extends CustomException {
    public UnauthorizedException(String message) {
        super("Unauthorized_Exception", message);
    }
}
