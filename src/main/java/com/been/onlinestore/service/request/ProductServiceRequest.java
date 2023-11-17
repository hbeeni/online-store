package com.been.onlinestore.service.request;

import com.been.onlinestore.domain.Category;
import com.been.onlinestore.domain.Product;
import com.been.onlinestore.domain.User;
import com.been.onlinestore.domain.constant.SaleStatus;

public record ProductServiceRequest(
        Long categoryId,
        String name,
        Integer price,
        String description,
        Integer stockQuantity,
        SaleStatus saleStatus,
        Integer deliveryFee,
        String imageUrl
) {

    public static ProductServiceRequest of(Long categoryId, String name, Integer price, String description, Integer stockQuantity, SaleStatus saleStatus, Integer deliveryFee, String imageUrl) {
        return new ProductServiceRequest(categoryId, name, price, description, stockQuantity, saleStatus, deliveryFee, imageUrl);
    }

    public Product toEntity(Category category, User seller) {
        return Product.of(
                category,
                seller,
                name,
                price,
                description,
                stockQuantity,
                0,
                saleStatus,
                deliveryFee,
                imageUrl
        );
    }
}
