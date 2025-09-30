package com.tnt.StayNest.exception;


public class BadRequestException extends CustomException  {
    public BadRequestException(String message) {
        super("Bad_Request_Exception",message);
    }
}
