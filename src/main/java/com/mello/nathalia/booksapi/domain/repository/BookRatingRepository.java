package com.mello.nathalia.booksapi.domain.repository;

import com.mello.nathalia.booksapi.domain.model.BookRating;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullMarked;
import java.math.BigDecimal;
import java.util.Optional;

@NullMarked
public interface BookRatingRepository {
    @NonNull
    Optional<BookRating> findByBookIdAndUserId(@NonNull Long bookId, @NonNull Long userId);
    @NonNull BookRating save(@NonNull BookRating bookRating);

    @NonNull BigDecimal sumRatingsByBookId(@NonNull Long bookId);
    int countRatingsByBookId(@NonNull Long bookId);
}
