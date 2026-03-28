package com.mello.nathalia.booksapi.api.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateCategoryRequest(
        @NotBlank(message = "Nome da categoria é obrigatório")
        @Size(max = 50, message = "Nome da categoria deve ter no máximo 50 caracteres")
        String name
) { }
