package com.mello.nathalia.booksapi.api.response;

public record BookResponse(
        long id,
        String title,
        String author,
        String category,
        double rating
){}
