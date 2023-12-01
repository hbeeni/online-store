package com.been.onlinestore.repository.querydsl.product;

import com.been.onlinestore.domain.Product;
import com.been.onlinestore.domain.User;
import com.been.onlinestore.domain.constant.SaleStatus;

import java.time.LocalDateTime;

public record AdminProductResponse(
        Long id,
        String category,
        Seller seller,
        String name,
        int price,
        String description,
        int stockQuantity,
        int salesVolume,
        SaleStatus saleStatus,
        int deliveryFee,
        String imageUrl,
        LocalDateTime createdAt,
        String createdBy,
        LocalDateTime modifiedAt,
        String modifiedBy
) {

    public static AdminProductResponse of(Long id, String category, Seller seller, String name, int price, String description, int stockQuantity, int salesVolume, SaleStatus saleStatus, int deliveryFee, String imageUrl, LocalDateTime createdAt, String createdBy, LocalDateTime modifiedAt, String modifiedBy) {
        return new AdminProductResponse(id, category, seller, name, price, description, stockQuantity, salesVolume, saleStatus, deliveryFee, imageUrl, createdAt, createdBy, modifiedAt, modifiedBy);
    }

    public static AdminProductResponse from(Product entity, String imageUrl) {
        return AdminProductResponse.of(
                entity.getId(),
                entity.getCategory().getName(),
                Seller.from(entity.getSeller()),
                entity.getName(),
                entity.getPrice(),
                entity.getDescription(),
                entity.getStockQuantity(),
                entity.getSalesVolume(),
                entity.getSaleStatus(),
                entity.getDeliveryFee(),
                imageUrl,
                entity.getCreatedAt(),
                entity.getCreatedBy(),
                entity.getModifiedAt(),
                entity.getModifiedBy()
        );
    }

    public record Seller(
            Long id,
            String uid
    ) {

        public static Seller of(Long id, String uid) {
            return new Seller(id, uid);
        }

        public static Seller from(User entity) {
            return Seller.of(
                    entity.getId(),
                    entity.getUid()
            );
        }
    }
}
