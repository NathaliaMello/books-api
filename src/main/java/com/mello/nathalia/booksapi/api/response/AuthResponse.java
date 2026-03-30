package com.mello.nathalia.booksapi.api.response;

public record AuthResponse (
        String token,
        String name,
        String email,
        String role
) { }
