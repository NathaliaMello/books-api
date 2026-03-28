package com.mello.nathalia.booksapi.infrastructure.persistence.impl;

import com.mello.nathalia.booksapi.domain.model.Book;
import com.mello.nathalia.booksapi.domain.repository.BookRepository;
import com.mello.nathalia.booksapi.infrastructure.persistence.BookJpaRepository;
import com.mello.nathalia.booksapi.infrastructure.persistence.BookSpecification;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class BookRepositoryImpl implements BookRepository {

    private final BookJpaRepository jpaRepository;

    public BookRepositoryImpl(BookJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Book save(Book book) {
        return jpaRepository.save(book);
    }

    @Override
    public Optional<Book> findById(Long id) {
        return jpaRepository.findById(id);
    }

    @Override
    public Optional<Book> findByIsbn(String isbn) {
        return jpaRepository.findByIsbn(isbn);
    }

    @Override
    public List<Book> findAll() {
        return jpaRepository.findAll();
    }

    @Override
    public List<Book> findByCategoryId(Long categoryId) {
        return jpaRepository.findByCategories_Id(categoryId);
    }

    @Override
    public List<Book> findWithFilters(String title, String author, Long categoryId) {
        return jpaRepository.findAll(
                BookSpecification.withFilters(title, author, categoryId)
        );
    }

    @Override
    public void delete(Book book) {
        jpaRepository.delete(book);
    }
}
