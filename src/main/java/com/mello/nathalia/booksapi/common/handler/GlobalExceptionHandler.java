package com.mello.nathalia.booksapi.common.handler;

import com.mello.nathalia.booksapi.common.exception.BookNotFoundException;
import com.mello.nathalia.booksapi.common.exception.DuplicateBookException;
import com.mello.nathalia.booksapi.common.exception.InvalidBookException;
import com.mello.nathalia.booksapi.common.response.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private ErrorResponse buildErrorResponse(String message, int status,
                                             String details, HttpServletRequest request) {
        return new ErrorResponse(
                LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME),
                status,
                message,
                details,
                request.getRequestURI()
        );
    }


    @ExceptionHandler(BookNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleBookNotFoundException
            (BookNotFoundException ex, HttpServletRequest request) {
        ErrorResponse error = buildErrorResponse(
                "Recurso não encontrado",
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),
                request
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);

    }

    @ExceptionHandler(DuplicateBookException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateBookException
            (DuplicateBookException ex, HttpServletRequest request) {

        ErrorResponse error = buildErrorResponse(
                "Livro já existe no sistema",
                HttpStatus.CONFLICT.value(),
                ex.getMessage(),
                request
        );

        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(InvalidBookException.class)
    public ResponseEntity<ErrorResponse> handleInvalidBookException
            (InvalidBookException ex, HttpServletRequest request) {

        ErrorResponse error =buildErrorResponse(
                "Dados inválidos fornecidos",
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                request
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
}
