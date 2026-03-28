package com.mello.nathalia.booksapi.infrastructure.persistence;

import com.mello.nathalia.booksapi.domain.model.Book;
import org.springframework.data.jpa.domain.Specification;

public class BookSpecification {
    private BookSpecification() {
        /* This utility class should not be instantiated */
    }

    public static Specification<Book> withFilters(String title, String author, Long categoryId) {
        return Specification
                .where(hasTitle(title))
                .and(hasAuthor(author))
                .and(hasCategoryId(categoryId));

    }

    private static Specification<Book> hasTitle(String title) {
        return (root, query, cb) -> {
            if (title == null || title.isBlank()) return null;
            return cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%");
        };
    }

    private static Specification<Book> hasAuthor(String author) {
        return (root, query, cb) -> {
            if (author == null || author.isBlank()) return null;
            return cb.like(cb.lower(root.get("author")), "%" + author.toLowerCase() + "%");
        };
    }

    private static Specification<Book> hasCategoryId(Long categoryId) {
        return (root, query, cb) -> {
            if (categoryId == null) return null;
            return cb.equal(root.get("category").get("id"), categoryId);
        };
    }

}
