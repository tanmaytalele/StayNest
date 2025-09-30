package com.tnt.StayNest.exception;

public class AccessDeniedException extends CustomException {
    public AccessDeniedException() {
        super("ACCESS_DENIED", "Access not allowed for this operation");
    }
}