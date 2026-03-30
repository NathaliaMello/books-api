package com.mello.nathalia.booksapi.domain.repository;

import com.mello.nathalia.booksapi.domain.model.Category;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullMarked;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

@NullMarked
public interface CategoryRepository {

    Category save(@NonNull Category category);
    Optional<Category> findById(@NonNull Long id);
    Optional<Category> findByName(@NonNull String name);
    List<Category> findAll();
    Page<Category> findAll(@NonNull Pageable pageable);
    boolean existsByName(@NonNull String name);
    void delete(@NonNull Category category);
}
