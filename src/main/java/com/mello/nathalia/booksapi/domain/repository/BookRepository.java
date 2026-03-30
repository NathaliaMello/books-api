package com.mello.nathalia.booksapi.domain.repository;

import com.mello.nathalia.booksapi.domain.model.Book;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Optional;

@NullMarked
public interface BookRepository {

    @NonNull
    Book save(@NonNull Book book);

    @NonNull
    Optional<Book> findById(@NonNull Long id);

    @NonNull
    Optional<Book> findByIsbn(@NonNull String isbn);

    @NonNull
    List<Book> findAll();

    @NonNull
    List<Book> findByCategoryId(@NonNull Long categoryId);

    @NonNull
    List<Book> findWithFilters(@Nullable String title, @Nullable String author, @Nullable Long categoryId);

    @NonNull
    List<Book> findWithCursor(
            @Nullable Long cursor,
            int size,
            @Nullable String title,
            @Nullable String author,
            @Nullable Long categoryId
    );

    void delete(@NonNull Book book);
}
