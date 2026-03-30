package com.mello.nathalia.booksapi.domain.service;

import com.mello.nathalia.booksapi.api.mapper.BookMapper;
import com.mello.nathalia.booksapi.api.request.CreateBookRequest;
import com.mello.nathalia.booksapi.api.request.UpdateBookRequest;
import com.mello.nathalia.booksapi.api.response.BookResponse;
import com.mello.nathalia.booksapi.common.exception.BookNotFoundException;
import com.mello.nathalia.booksapi.common.exception.DuplicateBookException;
import com.mello.nathalia.booksapi.common.exception.ErrorMessage;
import com.mello.nathalia.booksapi.common.response.CursorPageResponse;
import com.mello.nathalia.booksapi.domain.model.Book;
import com.mello.nathalia.booksapi.domain.model.Category;
import com.mello.nathalia.booksapi.domain.repository.BookRepository;
import com.mello.nathalia.booksapi.infrastructure.client.googlebooks.GoogleBooksClient;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BookService {

    private final BookMapper bookMapper;
    private final GoogleBooksClient googleBooksClient;
    private final BookRepository bookRepository;
    private final CategoryService categoryService;

    public BookService(BookMapper bookMapper, GoogleBooksClient googleBooksClient, BookRepository bookRepository,
                       CategoryService categoryService) {
        this.bookMapper = bookMapper;
        this.googleBooksClient = googleBooksClient;
        this.bookRepository = bookRepository;
        this.categoryService = categoryService;
    }

    public List<Book> findWithFilters(String title, String author, Long categoryId) {
        return bookRepository.findWithFilters(title, author, categoryId);
    }

    public Book getBookById(Long id) {
        log.debug("Buscando livro com ID: {}", id);
        return bookRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Livro com ID {} não encontrado", id);
                    return new BookNotFoundException(ErrorMessage.BOOK_NOT_FOUND.format(id));
                });
    }

    public List<Book> findByCategoryId(Long categoryId) {
        return bookRepository.findByCategoryId(categoryId);
    }

    public Book create(CreateBookRequest request) {
        log.info("Iniciando criação de novo livro com título: {}", request.title());

        Book book = bookMapper.toEntity(request);

        // usa o CategoryService ao invés do repositório diretamente
        Set<Category> categories = request.categoryIds().stream()
                .map(categoryService::findById)
                .collect(Collectors.toSet());

        book.setCategories(categories);

        googleBooksClient
                .filterByTitleAndAuthor(request.title(), request.author())
                .ifPresent(data -> {
                    book.setDescription(data.description());
                    if (book.getIsbn() == null && data.isbn() != null) {
                        book.setIsbn(data.isbn());
                    }
                });

        if (bookRepository.findByIsbn(book.getIsbn()).isPresent()) {
            throw new DuplicateBookException(
                    ErrorMessage.AUTHOR_DUPLICATE_BOOK.format(request.author(), request.title())
            );
        }

        Book saved = bookRepository.save(book);
        log.info("Livro criado com sucesso. ID: {}", saved.getId());
        return saved;
    }

    public Book update(Long id, UpdateBookRequest request) {
        log.debug("Atualizando livro com ID: {}", id);

        Book book = getBookById(id);

        Set<Category> categories = request.categoryIds().stream()
                .map(categoryService::findById)
                .collect(Collectors.toSet());

        bookMapper.updateEntity(request, book);
        book.setCategories(categories);

        return bookRepository.save(book);
    }

    public void delete(Long id) {
        log.debug("Deletando livro com ID: {}", id);
        Book book = getBookById(id);
        bookRepository.delete(book);
    }

    public CursorPageResponse<BookResponse> findWithCursor(
            @Nullable Long cursor, int size, String title, String author, Long categoryId) {
        List<Book> books = bookRepository.findWithCursor(cursor, size, title, author, categoryId);

        List<BookResponse> content = books.stream()
                .map(bookMapper::toResponse)
                .toList();

        Long nextCursor = books.isEmpty()
                ? null
                : books.getLast().getId();

        return CursorPageResponse.of(content, size, nextCursor);
    }

}
