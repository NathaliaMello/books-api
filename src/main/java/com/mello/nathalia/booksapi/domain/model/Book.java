package com.mello.nathalia.booksapi.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class Book {

    private long id;
    private String title;
    private String author;
    private String category;
    private double rating;

}
