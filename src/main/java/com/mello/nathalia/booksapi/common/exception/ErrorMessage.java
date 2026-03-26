package com.mello.nathalia.booksapi.common.exception;

import lombok.Getter;

@Getter
public enum ErrorMessage {
    BOOK_NOT_FOUND("Livro com o ID '%d' não encontrado"),
    INVALID_BOOK_DATA("Dados inválidos fornecidos"),
    AUTHOR_NOT_FOUND("Autor com o ID '%d' não encontrado"),
    BOOK_ALREADY_EXISTS("Livro com o título '%s' já existe"),
    RATING_IS_MANDATORY_BETWEEN_0_TO_5("A classificação deve estar entre 0 e 5"),
    AUTHOR_DUPLICATE_BOOK("O Autor '%s' já possui livro com o título '%s'");

    private final String message;

    ErrorMessage(String message) {
        this.message = message;
    }

    public String format(Object... args) {
        return String.format(message, args);
    }

}
