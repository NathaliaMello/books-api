package com.mello.nathalia.booksapi.domain.repository;

import com.mello.nathalia.booksapi.domain.model.User;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullMarked;

import java.util.Optional;

@NullMarked
public interface UserRepository {
    @NonNull
    User save(@NonNull User user);

    @NonNull
    Optional<User> findByEmail(@NonNull String email);

    boolean existsByEmail(@NonNull String email);
}
