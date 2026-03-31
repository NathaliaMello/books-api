package com.mello.nathalia.booksapi.api.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record RatingRequest(
        @NotNull(message = "Rating é obrigatório")
        @DecimalMin(value = "0.0", message = "Rating não pode ser menor que 0")
        @DecimalMax(value = "5.0", message = "Rating não pode ser maior que 5")
        BigDecimal rating
) { }
