package com.mello.nathalia.booksapi.infrastructure.persistence;

import com.mello.nathalia.booksapi.domain.model.User;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullMarked;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@NullMarked
@Repository
public interface UserJpaRepository extends JpaRepository<User, Long> {
    @NonNull
    Optional<User> findByEmail(@NonNull String email);

    boolean existsByEmail(@NonNull String email);
}
