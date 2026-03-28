package com.mello.nathalia.booksapi.domain.repository;

import com.mello.nathalia.booksapi.domain.model.Category;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository {
    Category save(Category category);
    Optional<Category> findById(Long id);
    Optional<Category> findByName(String name);
    List<Category> findAll();
    boolean existsByName(String name);
    void delete(Category category);
}
