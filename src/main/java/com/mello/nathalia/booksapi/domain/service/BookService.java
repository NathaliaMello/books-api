package com.mello.nathalia.booksapi.domain.service;

import com.mello.nathalia.booksapi.api.mapper.BookMapper;
import com.mello.nathalia.booksapi.api.request.BookRequest;
import com.mello.nathalia.booksapi.common.exception.BookNotFoundException;
import com.mello.nathalia.booksapi.common.exception.DuplicateBookException;
import com.mello.nathalia.booksapi.common.exception.ErrorMessage;
import com.mello.nathalia.booksapi.common.exception.InvalidBookException;
import com.mello.nathalia.booksapi.domain.model.Book;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class BookService {

    private static final String TECHNOLOGY_CATEGORY = "Technology";
    private static final String BUSINESS_CATEGORY = "Business";
    private static final String SCIENCE_CATEGORY = "Science";

    private Long nextId = 1L;

    private final List<Book> books = new ArrayList<>();
    private final BookMapper bookMapper;

    public BookService(BookMapper bookMapper) {
        this.bookMapper = bookMapper;
        initiateBooks();
    }

    private void initiateBooks() {
        // Tecnologia
        books.add(new Book(nextId++, "Clean Code", "Robert C. Martin", TECHNOLOGY_CATEGORY, 5));
        books.add(new Book(nextId++, "The Pragmatic Programmer", "David Thomas", TECHNOLOGY_CATEGORY, 5));
        books.add(new Book(nextId++, "Design Patterns", "Gang of Four", TECHNOLOGY_CATEGORY, 4.5));
        books.add(new Book(nextId++, "The Art of Computer Programming", "Donald Knuth", TECHNOLOGY_CATEGORY, 4));
        books.add(new Book(nextId++, "Introduction to Algorithms", "Thomas H. Cormen", TECHNOLOGY_CATEGORY, 4.8));
        books.add(new Book(nextId++, "Code Complete", "Steve McConnell", TECHNOLOGY_CATEGORY, 4.7));

        // Negócios
        books.add(new Book(nextId++, "The Lean Startup", "Eric Ries", BUSINESS_CATEGORY, 4.6));
        books.add(new Book(nextId++, "Good to Great", "Jim Collins", BUSINESS_CATEGORY, 4.5));
        books.add(new Book(nextId++, "The Innovator's Dilemma", "Clayton M. Christensen", BUSINESS_CATEGORY, 4.4));
        books.add(new Book(nextId++, "Thinking, Fast and Slow", "Daniel Kahneman", BUSINESS_CATEGORY, 4.7));
        books.add(new Book(nextId++, "Start with Why", "Simon Sinek", BUSINESS_CATEGORY, 4.5));

        // Ciência
        books.add(new Book(nextId++, "A Brief History of Time", "Stephen Hawking", SCIENCE_CATEGORY, 4.3));
        books.add(new Book(nextId++, "The Selfish Gene", "Richard Dawkins", SCIENCE_CATEGORY, 4.4));
        books.add(new Book(nextId++, "Sapiens", "Yuval Noah Harari", SCIENCE_CATEGORY, 4.8));
        books.add(new Book(nextId++, "Cosmos", "Carl Sagan", SCIENCE_CATEGORY, 4.9));
        books.add(new Book(nextId++, "The Origin of Species", "Charles Darwin", SCIENCE_CATEGORY, 4.2));


    }

    public List<Book> filterBooks(String title, String author, String category) {
        return books.stream()
                .filter(book ->  title == null || title.isBlank()
                    || book.getTitle().toLowerCase().contains(title.toLowerCase()))
                .filter(book -> author == null || author.isBlank()
                    || book.getAuthor().toLowerCase().contains(author.toLowerCase()))
                .filter(book -> category == null || category.isBlank()
                    || book.getCategory().toLowerCase().equalsIgnoreCase(category))
                .toList();
    }

    public Book getBookById(Long id) {
        log.debug("Buscando livro com ID: {}", id);

        return books.stream()
            .filter(book -> book.getId() == id)
            .findFirst()
            .map(book -> {
                log.debug("Livro encontrado: {}", book.getTitle());
                return book;
            })
            .orElseThrow(() -> {
                log.warn("Livro com ID {} não encontrado", id);
                return new BookNotFoundException(ErrorMessage.BOOK_NOT_FOUND.format(id));
            });
    }

    public Book createBook(BookRequest bookRequest) {
        log.info("Iniciando criação de novo livro com título: {}", bookRequest.title());

        try {
            validateDuplicateAuthorAndTitle(bookRequest.author(), bookRequest.title());
            validateRatingBookRequest(bookRequest);
            boolean bookExists = books.stream()
                    .anyMatch(book ->
                            book.getTitle().equalsIgnoreCase(bookRequest.title()) &&
                                    book.getAuthor().equalsIgnoreCase(bookRequest.author())
                    );
            if(bookExists) {
                log.warn("Tentativa de criar livro com título duplicado: {}", bookRequest.title());
                throw new DuplicateBookException(ErrorMessage.AUTHOR_DUPLICATE_BOOK
                        .format(bookRequest.author(), bookRequest.title()));
            }

            Book newBook = bookMapper.toEntity(bookRequest);
            newBook.setId(nextId++);
            books.add(newBook);
            return newBook;
        } catch (Exception e) {
            log.error("Erro ao criar livro com título: {}", bookRequest.title(), e);
            throw e;
        }
    }

    public Book updateBookById(Long id, BookRequest updatedBook) {
        Book book = getBookById(id);

        validateRatingBookRequest(updatedBook);

        bookMapper.updateBookFromRequest(updatedBook, book);
        return book;

    }

    public void deleteBookById(Long id) {
        Book book = getBookById(id);
        books.remove(book);
    }


    private void validateRatingBookRequest(BookRequest bookRequest) {
        if (bookRequest.rating() < 0 || bookRequest.rating() > 5) {
            throw new InvalidBookException(ErrorMessage.RATING_IS_MANDATORY_BETWEEN_0_TO_5.getMessage());
        }
    }

    private void validateDuplicateAuthorAndTitle(String author, String title) {
        books.stream()
            .filter(b -> b.getAuthor().equalsIgnoreCase(author))
            .filter(b -> b.getTitle().equalsIgnoreCase(title))
            .findAny()
            .ifPresent(book -> {
                throw new DuplicateBookException(
                        ErrorMessage.AUTHOR_DUPLICATE_BOOK.format(author, title)
                );
            });
    }


}
