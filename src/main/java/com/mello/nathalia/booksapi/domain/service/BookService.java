package com.mello.nathalia.booksapi.domain.service;

import com.mello.nathalia.booksapi.api.mapper.BookMapper;
import com.mello.nathalia.booksapi.api.request.BookRequest;
import com.mello.nathalia.booksapi.common.exception.BookNotFoundException;
import com.mello.nathalia.booksapi.common.exception.DuplicateBookException;
import com.mello.nathalia.booksapi.common.exception.InvalidBookException;
import com.mello.nathalia.booksapi.domain.model.Book;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

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

    public Optional<Book> getBookById(Long id) {
        return books.stream()
                .filter(book -> Objects.equals(book.getId(), id))
                .findFirst();
    }

    public Book createBook(BookRequest bookRequest) {
        validateBookRequest(bookRequest);

        boolean bookExists = books.stream()
                .anyMatch(book ->
                        book.getTitle().equalsIgnoreCase(bookRequest.title()) &&
                                book.getAuthor().equalsIgnoreCase(bookRequest.author())
                );

        if(bookExists) {
            throw new DuplicateBookException("Já existe um livro com o título: " + bookRequest.title() +
                    " do autor: " + bookRequest.author());
        }

        Book newBook = bookMapper.toEntity(bookRequest);
        newBook.setId(nextId++);
        books.add(newBook);
        return newBook;
    }

    public Book updateBookById(Long id, BookRequest updatedBook) {

        validateBookRequest(updatedBook);

        Book book = getBookById(id)
                .orElseThrow(() -> new BookNotFoundException("Livro com o ID " + id + " não encontrado"));

        boolean duplicatedExists = books.stream()
                .anyMatch(b -> !Objects.equals(b.getId(), id)
                        && b.getTitle().equalsIgnoreCase(updatedBook.title()));

        if (duplicatedExists) {
            throw new DuplicateBookException("Já existe outro livro com o título: " + updatedBook.title() + ".");
        }

        bookMapper.updateBookFromRequest(updatedBook, book);
        return book;
    }

    public void deleteBookById(Long id) {
        Book book = getBookById(id)
                .orElseThrow(() -> new BookNotFoundException("Livro com ID " + id + " não encontrado"));
        books.remove(book);
    }


    private void validateBookRequest(BookRequest bookRequest) {
        if (bookRequest.rating() < 0 || bookRequest.rating() > 5) {
            throw new InvalidBookException("A classificação deve estar entre 0 e 5");
        }
    }

}
