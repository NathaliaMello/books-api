package com.mello.nathalia.booksapi.api.controller;

import com.mello.nathalia.booksapi.api.mapper.CategoryMapper;
import com.mello.nathalia.booksapi.api.request.CreateCategoryRequest;
import com.mello.nathalia.booksapi.api.request.UpdateCategoryRequest;
import com.mello.nathalia.booksapi.api.response.CategoryResponse;
import com.mello.nathalia.booksapi.common.response.PageResponse;
import com.mello.nathalia.booksapi.domain.model.Category;
import com.mello.nathalia.booksapi.domain.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {

    private final CategoryService categoryService;
    private final CategoryMapper categoryMapper;

    public CategoryController(CategoryService categoryService,
                              CategoryMapper categoryMapper) {
        this.categoryService = categoryService;
        this.categoryMapper = categoryMapper;
    }

    @PostMapping
    public ResponseEntity<CategoryResponse> create(
            @RequestBody @Valid CreateCategoryRequest request) {
        Category category = categoryService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(categoryMapper.toResponse(category));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponse> update(
            @PathVariable Long id,
            @RequestBody @Valid UpdateCategoryRequest request) {
        Category category = categoryService.update(id, request);
        return ResponseEntity.ok(categoryMapper.toResponse(category));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponse> findById(@PathVariable Long id) {
        Category category = categoryService.findById(id);
        return ResponseEntity.ok(categoryMapper.toResponse(category));
    }

    @GetMapping
    public ResponseEntity<List<CategoryResponse>> findAll() {
        List<Category> categories = categoryService.findAll();
        return ResponseEntity.ok(categoryMapper.toResponseList(categories));
    }

    @GetMapping("/paginated")
    public ResponseEntity<PageResponse<CategoryResponse>> findAllPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(categoryService.findAllPaginated(page, size));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        categoryService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
