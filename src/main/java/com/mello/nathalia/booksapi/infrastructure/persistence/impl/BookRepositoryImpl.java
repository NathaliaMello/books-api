package com.mello.nathalia.booksapi.infrastructure.persistence.impl;

import com.mello.nathalia.booksapi.domain.model.Book;
import com.mello.nathalia.booksapi.domain.repository.BookRepository;
import com.mello.nathalia.booksapi.infrastructure.persistence.BookJpaRepository;
import com.mello.nathalia.booksapi.infrastructure.persistence.BookSpecification;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@NullMarked
@Component
public class BookRepositoryImpl implements BookRepository {

    private final BookJpaRepository jpaRepository;

    public BookRepositoryImpl(BookJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public @NonNull Book save(@NonNull Book book) {
        return jpaRepository.save(book);
    }

    @Override
    public @NonNull Optional<Book> findById(@NonNull Long id) {
        return jpaRepository.findById(id);
    }

    @Override
    public @NonNull Optional<Book> findByIsbn(@NonNull String isbn) {
        return jpaRepository.findByIsbn(isbn);
    }

    @Override
    public @NonNull List<Book> findAll() {
        return jpaRepository.findAll();
    }

    @Override
    public @NonNull List<Book> findWithFilters(@Nullable String title, @Nullable String author, @Nullable Long categoryId) {
        return jpaRepository.findAll(
                BookSpecification.withFilters(title, author, categoryId)
        );
    }

    @Override
    public @NonNull List<Book> findWithCursor(
            @Nullable Long cursor,
            int size,
            @Nullable String title,
            @Nullable String author,
            @Nullable Long categoryId) {

        List<Long> ids = jpaRepository.findIdsByCursor(
                cursor,
                size,
                title,
                author,
                categoryId,
                PageRequest.of(0, size)
        );

        if (ids.isEmpty()) return List.of();

        return jpaRepository.findByIdsWithCategories(ids);
    }

    @Override
    public List<Book> findByCategoryId(@NonNull Long categoryId) {
        return jpaRepository.findByCategories_Id(categoryId);
    }

    @Override
    public void delete(Book book) {
        jpaRepository.delete(book);
    }
}
