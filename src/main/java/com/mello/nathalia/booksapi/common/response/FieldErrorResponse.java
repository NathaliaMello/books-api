package com.mello.nathalia.booksapi.common.response;

public record FieldErrorResponse(
        String field,
        String message
) { }
