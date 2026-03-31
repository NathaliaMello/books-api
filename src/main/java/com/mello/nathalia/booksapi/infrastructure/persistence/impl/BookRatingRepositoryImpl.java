package com.mello.nathalia.booksapi.infrastructure.persistence.impl;

import com.mello.nathalia.booksapi.domain.model.BookRating;
import com.mello.nathalia.booksapi.domain.repository.BookRatingRepository;
import com.mello.nathalia.booksapi.infrastructure.persistence.BookRatingJpaRepository;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullMarked;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Optional;

@NullMarked
@Component
public class BookRatingRepositoryImpl implements BookRatingRepository {

    private final BookRatingJpaRepository jpaRepository;

    public BookRatingRepositoryImpl(BookRatingJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public @NonNull Optional<BookRating> findByBookIdAndUserId(
            @NonNull Long bookId, @NonNull Long userId) {
        return jpaRepository.findByBookIdAndUserId(bookId, userId);
    }

    @Override
    public @NonNull BookRating save(@NonNull BookRating bookRating) {
        return jpaRepository.save(bookRating);
    }

    @Override
    public @NonNull BigDecimal sumRatingsByBookId(@NonNull Long bookId) {
        return jpaRepository.sumRatingsByBookId(bookId);
    }

    @Override
    public int countRatingsByBookId(@NonNull Long bookId) {
        return jpaRepository.countRatingsByBookId(bookId);
    }
}