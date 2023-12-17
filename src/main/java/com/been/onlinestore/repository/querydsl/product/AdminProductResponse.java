package com.been.onlinestore.repository.querydsl.product;

import java.time.LocalDateTime;

import com.been.onlinestore.domain.Product;
import com.been.onlinestore.domain.constant.SaleStatus;

public record AdminProductResponse(
	Long id,
	String category,
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

	public static AdminProductResponse of(
		Long id, String category, String name, int price, String description, int stockQuantity, int salesVolume,
		SaleStatus saleStatus, int deliveryFee, String imageUrl, LocalDateTime createdAt, String createdBy,
		LocalDateTime modifiedAt, String modifiedBy
	) {
		return new AdminProductResponse(
			id, category, name, price, description, stockQuantity, salesVolume, saleStatus, deliveryFee, imageUrl,
			createdAt, createdBy, modifiedAt, modifiedBy
		);
	}

	public static AdminProductResponse from(Product entity, String imageUrl) {
		return AdminProductResponse.of(
			entity.getId(),
			entity.getCategory().getName(),
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
}
