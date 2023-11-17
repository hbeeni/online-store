package com.been.onlinestore.service.response;

import com.been.onlinestore.domain.Category;

public record CategoryResponse(
        Long id,
        String name,
        String description,
        int productCount
) {

    public static CategoryResponse of(Long id, String name, String description, int productCount) {
        return new CategoryResponse(id, name, description, productCount);
    }

    public static CategoryResponse from(Category entity) {
        return CategoryResponse.of(
                entity.getId(),
                entity.getName(),
                entity.getDescription(),
                entity.getProducts().size()
        );
    }
}
