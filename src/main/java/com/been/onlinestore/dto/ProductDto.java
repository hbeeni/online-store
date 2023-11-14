package com.been.onlinestore.dto;

import com.been.onlinestore.domain.Category;
import com.been.onlinestore.domain.Product;
import com.been.onlinestore.domain.User;
import com.been.onlinestore.domain.constant.SaleStatus;

import java.time.LocalDateTime;

public record ProductDto(
        Long id,
        CategoryDto categoryDto,
        UserDto sellerDto,
        String name,
        Integer price,
        String description,
        Integer stockQuantity,
        Integer salesVolume,
        SaleStatus saleStatus,
        Integer deliveryFee,
        String imageUrl,
        LocalDateTime createdAt,
        String createdBy,
        LocalDateTime modifiedAt,
        String modifiedBy
) {

    public static ProductDto of(Long id, CategoryDto categoryDto, UserDto sellerDto, String name, Integer price, String description, Integer stockQuantity, Integer salesVolume, SaleStatus saleStatus, Integer deliveryFee, String imageUrl, LocalDateTime createdAt, String createdBy, LocalDateTime modifiedAt, String modifiedBy) {
        return new ProductDto(id, categoryDto, sellerDto, name, price, description, stockQuantity, salesVolume, saleStatus, deliveryFee, imageUrl, createdAt, createdBy, modifiedAt, modifiedBy);
    }

    public static ProductDto from(Product entity) {
        return ProductDto.of(
                entity.getId(),
                CategoryDto.from(entity.getCategory()),
                UserDto.from(entity.getSeller()),
                entity.getName(),
                entity.getPrice(),
                entity.getDescription(),
                entity.getStockQuantity(),
                entity.getSalesVolume(),
                entity.getSaleStatus(),
                entity.getDeliveryFee(),
                entity.getImageUrl(),
                entity.getCreatedAt(),
                entity.getCreatedBy(),
                entity.getModifiedAt(),
                entity.getModifiedBy()
        );
    }

    public Product toEntity(Category category, User seller) {
        return Product.of(
                category,
                seller,
                name,
                price,
                description,
                stockQuantity,
                salesVolume,
                saleStatus,
                deliveryFee,
                imageUrl
        );
    }
}
