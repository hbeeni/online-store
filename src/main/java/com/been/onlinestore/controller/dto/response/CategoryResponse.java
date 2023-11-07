package com.been.onlinestore.controller.dto.response;

import com.been.onlinestore.dto.CategoryDto;

public record CategoryResponse(
        Long id,
        String name,
        String description,
        int productCount
) {

    public static CategoryResponse of(Long id, String name, String description, int productCount) {
        return new CategoryResponse(id, name, description, productCount);
    }

    public static CategoryResponse from(CategoryDto dto) {
        return CategoryResponse.of(
                dto.id(),
                dto.name(),
                dto.description(),
                dto.productCount()
        );
    }
}
