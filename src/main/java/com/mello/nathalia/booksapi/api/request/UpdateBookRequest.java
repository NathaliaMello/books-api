package com.mello.nathalia.booksapi.api.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.Set;

public record UpdateBookRequest(
        @NotBlank(message = "Título é obrigatório")
        String title,

        @NotBlank(message = "Autor é obrigatório")
        String author,

        @NotEmpty(message = "Pelo menos uma categoria é obrigatória")
        Set<Long> categoryIds,

        @DecimalMin(value = "0.0", message = "Rating não pode ser negativo")
        @DecimalMax(value = "5.0", message = "Rating não pode ser maior que 5")
        Double rating
) { }
