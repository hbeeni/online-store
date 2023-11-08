package com.been.onlinestore.dto;

import com.been.onlinestore.domain.Category;
import com.been.onlinestore.domain.Product;
import com.been.onlinestore.domain.User;
import com.been.onlinestore.domain.constant.SaleStatus;

import java.time.LocalDateTime;

public record ProductDto(
        Long id,
        CategoryDto categoryDto,
        UserDto userDto,
        String name,
        Integer price,
        String description,
        Integer stockQuantity,
        Integer salesVolume,
        SaleStatus saleStatus,
        String imageUrl,
        LocalDateTime createdAt,
        String createdBy,
        LocalDateTime modifiedAt,
        String modifiedBy
) {

    public static ProductDto of(Long id, CategoryDto categoryDto, UserDto userDto, String name, Integer price, String description, Integer stockQuantity, Integer salesVolume, SaleStatus saleStatus, String imageUrl, LocalDateTime createdAt, String createdBy, LocalDateTime modifiedAt, String modifiedBy) {
        return new ProductDto(id, categoryDto, userDto, name, price, description, stockQuantity, salesVolume, saleStatus, imageUrl, createdAt, createdBy, modifiedAt, modifiedBy);
    }

    public static ProductDto from(Product entity) {
        return ProductDto.of(
                entity.getId(),
                CategoryDto.from(entity.getCategory()),
                UserDto.from(entity.getUser()),
                entity.getName(),
                entity.getPrice(),
                entity.getDescription(),
                entity.getStockQuantity(),
                entity.getSalesVolume(),
                entity.getSaleStatus(),
                entity.getImageUrl(),
                entity.getCreatedAt(),
                entity.getCreatedBy(),
                entity.getModifiedAt(),
                entity.getModifiedBy()
        );
    }

    public Product toEntity(Category category, User user) {
        return Product.of(
                category,
                user,
                name,
                price,
                description,
                stockQuantity,
                salesVolume,
                saleStatus,
                imageUrl
        );
    }
}
