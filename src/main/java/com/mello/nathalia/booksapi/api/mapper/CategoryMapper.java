package com.mello.nathalia.booksapi.api.mapper;

import com.mello.nathalia.booksapi.api.request.CreateCategoryRequest;
import com.mello.nathalia.booksapi.api.request.UpdateCategoryRequest;
import com.mello.nathalia.booksapi.api.response.CategoryResponse;
import com.mello.nathalia.booksapi.domain.model.Category;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface CategoryMapper {
    Category toEntity(CreateCategoryRequest request);
    void updateEntity(UpdateCategoryRequest request, @MappingTarget Category category);
    CategoryResponse toResponse(Category category);
    List<CategoryResponse> toResponseList(List<Category> categories);
}
