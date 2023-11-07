package com.been.onlinestore.dto;

import com.been.onlinestore.domain.Category;

public record CategoryDto(
        Long id,
        String name,
        String description,
        int productCount
) {

    public static CategoryDto of(String name, String description) {
        return CategoryDto.of(null, name, description, 0);
    }

    public static CategoryDto of(Long id, String name, String description, int productCount) {
        return new CategoryDto(id, name, description, productCount);
    }

    public static CategoryDto from(Category category) {
        return CategoryDto.of(category.getId(), category.getName(), category.getDescription(), category.getProducts().size());
    }

    public Category toEntity() {
        return Category.of(name, description);
    }
}
