package com.mello.nathalia.booksapi.common.exception;

import java.io.Serial;

public class DuplicateBookException extends RuntimeException{

    @Serial
    private static final long serialVersionUID = 1L;

    public DuplicateBookException(String message) {
        super(message);
    }

    public DuplicateBookException(String message, Throwable cause) {
        super(message, cause);
    }
}
