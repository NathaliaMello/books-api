package com.mello.nathalia.booksapi.infrastructure.persistence.impl;

import com.mello.nathalia.booksapi.domain.model.Category;
import com.mello.nathalia.booksapi.domain.repository.CategoryRepository;
import com.mello.nathalia.booksapi.infrastructure.persistence.CategoryJpaRepository;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class CategoryRepositoryImpl implements CategoryRepository {

    private final CategoryJpaRepository jpaRepository;

    public CategoryRepositoryImpl(CategoryJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public @NonNull Category save(@NonNull Category category) {
        return jpaRepository.save(category);
    }

    @Override
    public @NonNull Optional<Category> findById(@NonNull Long id) {
        return jpaRepository.findById(id);
    }

    @Override
    public @NonNull Optional<Category> findByName(@NonNull String name) {
        return jpaRepository.findByName(name);
    }

    @Override
    public @NonNull List<Category> findAll() {
        return jpaRepository.findAll();
    }

    @Override
    public @NonNull Page<Category> findAll(@NonNull Pageable pageable) {
        return jpaRepository.findAll(pageable);
    }

    @Override
    public boolean existsByName(@NonNull String name) {
        return jpaRepository.existsByName(name);
    }

    @Override
    public void delete(@NonNull Category category) {
        jpaRepository.delete(category);
    }
}
