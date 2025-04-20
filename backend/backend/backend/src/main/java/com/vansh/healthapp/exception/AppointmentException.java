package com.vansh.healthapp.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class AppointmentException extends RuntimeException {

    private final String errorCode;
    private final HttpStatus status;

    public static final String SLOT_UNAVAILABLE = "SLOT_UNAVAILABLE";
    public static final String INVALID_STATUS_CHANGE = "INVALID_STATUS_CHANGE";
    public static final String PAST_DATE_SELECTED = "PAST_DATE_SELECTED";
    public static final String DOCTOR_UNAVAILABLE = "DOCTOR_UNAVAILABLE";
    public static final String OVERLAPPING_APPOINTMENT = "OVERLAPPING_APPOINTMENT";

    public AppointmentException(String message) {
        super(message);
        this.errorCode = "APPOINTMENT_ERROR";
        this.status = HttpStatus.BAD_REQUEST;
    }

    public AppointmentException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
        this.status = HttpStatus.BAD_REQUEST;
    }

    public AppointmentException(String message, String errorCode, HttpStatus status) {
        super(message);
        this.errorCode = errorCode;
        this.status = status;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public HttpStatus getStatus() {
        return status;
    }
} 