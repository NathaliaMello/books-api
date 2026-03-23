package com.mello.nathalia.booksapi.api.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record BookRequest(
        @NotBlank String title,
        @NotBlank String author,
        @NotBlank String category,
        @NotNull double rating
) {}
