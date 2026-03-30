package com.mello.nathalia.booksapi.domain.service;

import com.mello.nathalia.booksapi.api.mapper.CategoryMapper;
import com.mello.nathalia.booksapi.api.request.CreateCategoryRequest;
import com.mello.nathalia.booksapi.api.request.UpdateCategoryRequest;
import com.mello.nathalia.booksapi.api.response.CategoryResponse;
import com.mello.nathalia.booksapi.common.exception.CategoryAlreadyExistsException;
import com.mello.nathalia.booksapi.common.exception.CategoryNotFoundException;
import com.mello.nathalia.booksapi.common.response.PageResponse;
import com.mello.nathalia.booksapi.domain.model.Category;
import com.mello.nathalia.booksapi.domain.repository.CategoryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    private static final String CATEGORIA_NOT_FOUND = "Categoria não encontrada com id: ";

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public CategoryService(CategoryRepository categoryRepository,
                           CategoryMapper categoryMapper) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
    }

    public Category create(CreateCategoryRequest request) {
        if (categoryRepository.existsByName(request.name())) {
            throw new CategoryAlreadyExistsException(
                    "Já existe uma categoria com o nome: " + request.name()
            );
        }
        return categoryRepository.save(categoryMapper.toEntity(request));
    }

    public Category update(Long id, UpdateCategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(
                        CATEGORIA_NOT_FOUND + id
                ));

        if (categoryRepository.existsByName(request.name())) {
            throw new CategoryAlreadyExistsException(
                    "Já existe uma categoria com o nome: " + request.name()
            );
        }

        categoryMapper.updateEntity(request, category);
        return categoryRepository.save(category);
    }

    public Category findById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(
                        CATEGORIA_NOT_FOUND + id
                ));
    }

    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    public PageResponse<CategoryResponse> findAllPaginated(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        Page<Category> result = categoryRepository.findAll(pageable);
        Page<CategoryResponse> mapped = result.map(categoryMapper::toResponse);
        return PageResponse.from(mapped);
    }

    public void delete(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(
                        CATEGORIA_NOT_FOUND + id
                ));
        categoryRepository.delete(category);
    }
}
