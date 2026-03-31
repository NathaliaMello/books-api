package com.mello.nathalia.booksapi.infrastructure.persistence;

import com.mello.nathalia.booksapi.domain.model.BookRating;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullMarked;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;

@NullMarked
@Repository
public interface BookRatingJpaRepository extends JpaRepository<BookRating, Long> {
    @NonNull
    Optional<BookRating> findByBookIdAndUserId(@NonNull Long bookId, @NonNull Long userId);

    @Query("SELECT COALESCE(SUM(r.rating), 0) FROM BookRating r WHERE r.book.id = :bookId")
    @NonNull
    BigDecimal sumRatingsByBookId(@Param("bookId") Long bookId);

    @Query("SELECT COUNT(r) FROM BookRating r WHERE r.book.id = :bookId")
    int countRatingsByBookId(@Param("bookId") Long bookId);
}
