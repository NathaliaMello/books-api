package com.mello.nathalia.booksapi.common.response;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public record ErrorResponse(
        String timestamp,
        int status,
        String error,
        String message,
        String path,
        List<FieldErrorResponse> fields
) {
    public ErrorResponse(int status, String error, String message, String path) {
        this(
                LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME),
                status,
                error,
                message,
                path,
                null
        );
    }

    public ErrorResponse(int status, String error, String message, String path,
                         List<FieldErrorResponse> fields) {
        this(
                LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME),
                status,
                error,
                message,
                path,
                fields
        );
    }
}