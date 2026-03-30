package com.mello.nathalia.booksapi.infrastructure.persistence.impl;

import com.mello.nathalia.booksapi.domain.model.User;
import com.mello.nathalia.booksapi.domain.repository.UserRepository;
import com.mello.nathalia.booksapi.infrastructure.persistence.UserJpaRepository;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullMarked;
import org.springframework.stereotype.Component;

import java.util.Optional;

@NullMarked
@Component
public class UserRepositoryImpl implements UserRepository {
    private final UserJpaRepository jpaRepository;

    public UserRepositoryImpl(UserJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public @NonNull User save(@NonNull User user) {
        return jpaRepository.save(user);
    }

    @Override
    public @NonNull Optional<User> findByEmail(@NonNull String email) {
        return jpaRepository.findByEmail(email);
    }

    @Override
    public boolean existsByEmail(@NonNull String email) {
        return jpaRepository.existsByEmail(email);
    }
}
