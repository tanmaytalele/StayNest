package com.tnt.StayNest.exception;


public class InvalidCancellationException extends CustomException {
    public InvalidCancellationException() {
        super("INVALID_CANCELLATION", "Cannot cancel past booking");
    }
}