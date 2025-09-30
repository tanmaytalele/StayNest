package com.tnt.StayNest.exception;

public class DateUnavailableException extends CustomException {
    public DateUnavailableException() {
        super("DATE_UNAVAILABLE", "Dates unavailable for booking");
    }
}