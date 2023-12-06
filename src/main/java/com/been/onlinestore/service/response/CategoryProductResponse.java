package com.been.onlinestore.service.response;

import com.been.onlinestore.domain.Product;

public record CategoryProductResponse(
	Long id,
	String category,
	String name,
	int price,
	String description,
	int deliveryFee,
	String imageUrl
) {

	public static CategoryProductResponse of(Long id, String category, String name, int price, String description,
		int deliveryFee, String imageUrl) {
		return new CategoryProductResponse(id, category, name, price, description, deliveryFee, imageUrl);
	}

	public static CategoryProductResponse from(Product entity, String imageUrl) {
		return CategoryProductResponse.of(
			entity.getId(),
			entity.getCategory().getName(),
			entity.getName(),
			entity.getPrice(),
			entity.getDescription(),
			entity.getDeliveryFee(),
			imageUrl
		);
	}
}
