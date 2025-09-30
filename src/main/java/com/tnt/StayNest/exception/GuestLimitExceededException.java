package com.tnt.StayNest.exception;

public class GuestLimitExceededException extends CustomException {
    public GuestLimitExceededException() {
        super("GUEST_LIMIT_EXCEEDED", "Guest count exceeds max allowed");
    }
}
