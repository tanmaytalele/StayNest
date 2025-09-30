package com.tnt.StayNest.exception;

public class InvalidPropertyValueException extends CustomException {
    public InvalidPropertyValueException(String message) {
        super("Invalid_Property_Value_Exception",message);
    }
}
