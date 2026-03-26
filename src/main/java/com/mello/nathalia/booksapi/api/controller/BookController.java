package com.mello.nathalia.booksapi.api.controller;

import com.mello.nathalia.booksapi.api.mapper.BookMapper;
import com.mello.nathalia.booksapi.api.request.BookRequest;
import com.mello.nathalia.booksapi.api.response.BookResponse;
import com.mello.nathalia.booksapi.domain.model.Book;
import com.mello.nathalia.booksapi.domain.service.BookService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/books")
public class BookController {


    private final BookService bookService;
    private final BookMapper bookMapper;

    public BookController(BookService bookService, BookMapper bookMapper) {
        this.bookService = bookService;
        this.bookMapper = bookMapper;
    }

    @GetMapping
    public ResponseEntity<List<BookResponse>> getAllBooks(
            @RequestParam(name = "title", required = false) String title,
            @RequestParam(name = "author", required = false) String author,
            @RequestParam(name = "category", required = false) String category
    ) {
        List<Book> books = bookService.filterBooks(title, author, category);
        List<BookResponse> response = books.stream()
                .map(bookMapper::toResponse)
                .toList();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Book> getBookById(@PathVariable Long id) {
        return ResponseEntity.ok(bookService.getBookById(id));
    }

    @PostMapping
    public ResponseEntity<BookResponse> createBook(@Valid @RequestBody BookRequest bookRequest) {
        Book newBook = bookService.createBook(bookRequest);
        return ResponseEntity.ok(bookMapper.toResponse(newBook));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BookResponse> updateBookById(
            @PathVariable Long id,
            @Valid @RequestBody BookRequest updateBook) {
        Book updatedBook = bookService.updateBookById(id, updateBook);
        return ResponseEntity.ok(bookMapper.toResponse(updatedBook));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBookById(@PathVariable Long id) {
        bookService.deleteBookById(id);
        return ResponseEntity.noContent().build();
    }

}
