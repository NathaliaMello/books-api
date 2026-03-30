package com.mello.nathalia.booksapi.infrastructure.persistence;

import com.mello.nathalia.booksapi.domain.model.Book;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@NullMarked
@Repository
public interface BookJpaRepository extends JpaRepository<Book, Long>, JpaSpecificationExecutor<Book> {
    @NonNull
    Optional<Book> findByIsbn(@NonNull String isbn);
    
    @NonNull 
    List<Book> findByCategories_Id(@NonNull Long categoryId);

    @Query("""
    SELECT b.id FROM Book b
    WHERE (:cursor IS NULL OR b.id > :cursor)
    AND (:title IS NULL OR LOWER(b.title) LIKE LOWER(CONCAT('%', :title, '%')))
    AND (:author IS NULL OR LOWER(b.author) LIKE LOWER(CONCAT('%', :author, '%')))
    AND (:categoryId IS NULL OR EXISTS (
        SELECT 1 FROM b.categories c WHERE c.id = :categoryId
    ))
    ORDER BY b.id ASC
""")
    List<Long> findIdsByCursor(
            @Nullable @Param("cursor") Long cursor,
            @Param("size") int size,
            @Nullable @Param("title") String title,
            @Nullable @Param("author") String author,
            @Nullable @Param("categoryId") Long categoryId,
            Pageable pageable
    );

    @Query("""
        SELECT DISTINCT b FROM Book b
        LEFT JOIN FETCH b.categories
        WHERE b.id IN :ids
        ORDER BY b.id ASC
    """)
    List<Book> findByIdsWithCategories(@Param("ids") List<Long> ids);
}