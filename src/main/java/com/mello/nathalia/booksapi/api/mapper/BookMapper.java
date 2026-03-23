package com.mello.nathalia.booksapi.api.mapper;

import com.mello.nathalia.booksapi.api.request.BookRequest;
import com.mello.nathalia.booksapi.api.response.BookResponse;
import com.mello.nathalia.booksapi.domain.model.Book;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface BookMapper {
    Book toEntity(BookRequest bookRequest);
    BookResponse toResponse(Book book);
    void updateBookFromRequest(BookRequest bookRequest, @MappingTarget Book book);

}
