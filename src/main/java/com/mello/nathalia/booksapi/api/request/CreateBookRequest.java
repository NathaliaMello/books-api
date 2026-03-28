package com.mello.nathalia.booksapi.api.request;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.*;

import java.util.Set;

public record CreateBookRequest(
        @NotBlank(message = "Título é obrigatório")
        String title,

        @NotBlank(message = "Autor é obrigatório")
        String author,

        @Nullable
        String isbn,

        @DecimalMin(value = "0.0", message = "Rating não pode ser negativo")
        @DecimalMax(value = "5.0", message = "Rating não pode ser maior que 5")
        Double rating,

        @NotEmpty(message = "Pelo menos uma categoria é obrigatória")
        Set<Long> categoryIds
) {}
