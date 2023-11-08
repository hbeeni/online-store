package com.been.onlinestore.dto;

import com.been.onlinestore.domain.Category;

import java.time.LocalDateTime;

public record CategoryDto(
        Long id,
        String name,
        String description,
        Integer productCount,
        LocalDateTime createdAt,
        String createdBy,
        LocalDateTime modifiedAt,
        String modifiedBy
) {

    public static CategoryDto of(String name, String description) {
        return CategoryDto.of(null, name, description, null, null, null, null, null);
    }

    public static CategoryDto of(Long id, String name, String description, Integer productCount, LocalDateTime createdAt, String createdBy, LocalDateTime modifiedAt, String modifiedBy) {
        return new CategoryDto(id, name, description, productCount, createdAt, createdBy, modifiedAt, modifiedBy);
    }

    public static CategoryDto from(Category category) {
        return CategoryDto.of(
                category.getId(),
                category.getName(),
                category.getDescription(),
                category.getProducts().size(),
                category.getCreatedAt(),
                category.getCreatedBy(),
                category.getModifiedAt(),
                category.getModifiedBy()
        );
    }

    public Category toEntity() {
        return Category.of(name, description);
    }
}
