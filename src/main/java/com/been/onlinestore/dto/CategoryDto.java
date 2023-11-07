package com.been.onlinestore.dto;

import com.been.onlinestore.domain.Category;

public record CategoryDto(
        Long id,
        String name,
        String description,
        int productCount
) {

    public static CategoryDto of(Category category, Integer productCount) {
        return CategoryDto.of(category.getId(), category.getName(), category.getDescription(), productCount);
    }

    public static CategoryDto of(Long id, String name, String description, int productCount) {
        return new CategoryDto(id, name, description, productCount);
    }

    public Category toEntity() {
        return Category.of(name, description);
    }
}
