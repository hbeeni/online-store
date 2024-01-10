package com.been.onlinestore.service.dto.response;

import com.been.onlinestore.domain.Product;
import com.been.onlinestore.domain.constant.SaleStatus;

public record CategoryProductResponse(
	Long id,
	String category,
	String name,
	int price,
	String description,
	SaleStatus saleStatus,
	int deliveryFee,
	String imageUrl
) {

	public static CategoryProductResponse of(
		Long id, String category, String name, int price, String description,
		SaleStatus saleStatus, int deliveryFee, String imageUrl
	) {
		return new CategoryProductResponse(id, category, name, price, description, saleStatus, deliveryFee, imageUrl);
	}

	public static CategoryProductResponse from(Product entity, String imageUrl) {
		return CategoryProductResponse.of(
			entity.getId(),
			entity.getCategory().getName(),
			entity.getName(),
			entity.getPrice(),
			entity.getDescription(),
			entity.getSaleStatus(),
			entity.getDeliveryFee(),
			imageUrl
		);
	}
}
