package com.mello.nathalia.booksapi.infrastructure.persistence;

import com.mello.nathalia.booksapi.domain.model.Category;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryJpaRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByName(String name);
    boolean existsByName(String name);
    @NonNull Page<Category> findAll(@NonNull Pageable pageable);
}
