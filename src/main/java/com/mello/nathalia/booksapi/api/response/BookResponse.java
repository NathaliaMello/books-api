package com.mello.nathalia.booksapi.api.response;

import java.time.LocalDateTime;
import java.util.Set;

public record BookResponse(
        Long id,
        String title,
        String author,
        Set<CategoryResponse> categories,
        String isbn,
        String description,
        Double rating,
        LocalDateTime createdAt
) {}
