package com.been.onlinestore.controller.dto;

import com.been.onlinestore.domain.constant.SaleStatus;
import com.been.onlinestore.service.request.ProductServiceRequest;
import lombok.Builder;

public record ProductRequest() {

    @Builder
    public record Create(
            Long categoryId,
            String name,
            Integer price,
            String description,
            Integer stockQuantity,
            SaleStatus saleStatus,
            Integer deliveryFee,
            String imageUrl
    ) {

        public ProductServiceRequest toServiceRequest() {
            return ProductServiceRequest.of(
                    categoryId,
                    name,
                    price,
                    description,
                    stockQuantity,
                    saleStatus,
                    deliveryFee,
                    imageUrl
            );
        }
    }

    @Builder
    public record Update(
            Long categoryId,
            String name,
            Integer price,
            String description,
            Integer stockQuantity,
            SaleStatus saleStatus,
            Integer deliveryFee,
            String imageUrl
    ) {

        public ProductServiceRequest toServiceRequest() {
            return ProductServiceRequest.of(
                    categoryId,
                    name,
                    price,
                    description,
                    stockQuantity,
                    saleStatus,
                    deliveryFee,
                    imageUrl
            );
        }
    }
}
