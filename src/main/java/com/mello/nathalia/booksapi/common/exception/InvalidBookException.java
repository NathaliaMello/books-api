package com.mello.nathalia.booksapi.common.exception;

import java.io.Serial;

public class InvalidBookException extends RuntimeException{

    @Serial
    private static final long serialVersionUID = 1L;

    public InvalidBookException(String message) {
        super(message);
    }

    public InvalidBookException(String message, Throwable cause) {
        super(message, cause);
    }
}
