package com.mello.nathalia.booksapi.api.mapper;

import com.mello.nathalia.booksapi.api.request.CreateBookRequest;
import com.mello.nathalia.booksapi.api.request.UpdateBookRequest;
import com.mello.nathalia.booksapi.api.response.BookResponse;
import com.mello.nathalia.booksapi.domain.model.Book;
import org.mapstruct.*;

import java.util.List;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface BookMapper {
    @Mapping(target = "categories", ignore = true)
    @Mapping(target = "description", ignore = true)
    Book toEntity(CreateBookRequest request);

    @Mapping(target = "categories", ignore = true)
    @Mapping(target = "description", ignore = true)
    @Mapping(target = "isbn", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void updateEntity(UpdateBookRequest request, @MappingTarget Book book);

    BookResponse toResponse(Book book);

    List<BookResponse> toResponseList(List<Book> books);

}
