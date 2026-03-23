package com.mello.nathalia.booksapi.common.exception;

import java.io.Serial;

public class BookNotFoundException extends RuntimeException{

    @Serial
    private static final long serialVersionUID = 1L;

    public BookNotFoundException(String message) {
        super(message);
    }
    public BookNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
