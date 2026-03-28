package com.mello.nathalia.booksapi.domain.repository;

import com.mello.nathalia.booksapi.domain.model.Book;

import java.util.List;
import java.util.Optional;

public interface BookRepository {
    Book save(Book book);
    Optional<Book> findById(Long id);
    Optional<Book> findByIsbn(String isbn);
    List<Book> findAll();
    List<Book> findByCategoryId(Long categoryId);
    List<Book> findWithFilters(String title, String author, Long categoryId);
    void delete(Book book);
}
