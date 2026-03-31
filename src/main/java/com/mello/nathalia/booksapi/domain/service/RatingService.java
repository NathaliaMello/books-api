package com.mello.nathalia.booksapi.domain.service;

import com.mello.nathalia.booksapi.common.exception.BookNotFoundException;
import com.mello.nathalia.booksapi.common.exception.ErrorMessage;
import com.mello.nathalia.booksapi.domain.model.Book;
import com.mello.nathalia.booksapi.domain.model.BookRating;
import com.mello.nathalia.booksapi.domain.model.User;
import com.mello.nathalia.booksapi.domain.repository.BookRatingRepository;
import com.mello.nathalia.booksapi.domain.repository.BookRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class RatingService {

    private final BookRepository bookRepository;
    private final BookRatingRepository bookRatingRepository;

    @Transactional
    public Book rate(Long bookId, BigDecimal newRating, User user) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException(
                        ErrorMessage.BOOK_NOT_FOUND.format(bookId)
                ));

        BookRating bookRating = bookRatingRepository
                .findByBookIdAndUserId(bookId, user.getId())
                .orElseGet(() -> {
                    BookRating novo = new BookRating();
                    novo.setBook(book);
                    novo.setUser(user);
                    return novo;
                });

        bookRating.setRating(newRating);
        bookRatingRepository.save(bookRating);

        BigDecimal sum = bookRatingRepository.sumRatingsByBookId(bookId);
        int count = bookRatingRepository.countRatingsByBookId(bookId);

        BigDecimal newAverage = count == 0
                ? BigDecimal.ZERO
                : sum.divide(BigDecimal.valueOf(count), 1, RoundingMode.HALF_UP)
                    .min(BigDecimal.valueOf(5.0));

        book.setRating(newAverage);
        book.setRatingCount(count);

        Book saved = bookRepository.save(book);
        log.info("Rating do livro {} atualizado para {} ({} votos)",
                bookId, saved.getRating(), saved.getRatingCount());
        return saved;
    }

    public Optional<BigDecimal> getMyRating(Long bookId, User user) {
        return bookRatingRepository.findByBookIdAndUserId(bookId, user.getId())
                .map(BookRating::getRating);
    }
}
