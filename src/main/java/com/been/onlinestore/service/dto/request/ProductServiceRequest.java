package com.been.onlinestore.service.dto.request;

import com.been.onlinestore.domain.Category;
import com.been.onlinestore.domain.Product;
import com.been.onlinestore.domain.constant.SaleStatus;

public record ProductServiceRequest() {

	public record Create(
		Long categoryId,
		String name,
		Integer price,
		String description,
		Integer stockQuantity,
		SaleStatus saleStatus,
		Integer deliveryFee
	) {

		public static Create of(
			Long categoryId, String name, Integer price, String description, Integer stockQuantity,
			SaleStatus saleStatus, Integer deliveryFee
		) {
			return new Create(categoryId, name, price, description, stockQuantity, saleStatus, deliveryFee);
		}

		public Product toEntity(Category category, String imageName) {
			return toEntity(category, saleStatus, imageName);
		}

		public Product toEntity(Category category, SaleStatus saleStatus, String imageName) {
			return Product.of(
				category,
				name,
				price,
				description,
				stockQuantity,
				0,
				saleStatus,
				deliveryFee,
				imageName
			);
		}
	}

	public record Update(
		Long categoryId,
		String name,
		Integer price,
		String description,
		Integer stockQuantity,
		SaleStatus saleStatus,
		Integer deliveryFee
	) {

		public static Update of(
			Long categoryId, String name, Integer price, String description, Integer stockQuantity,
			SaleStatus saleStatus, Integer deliveryFee
		) {
			return new Update(categoryId, name, price, description, stockQuantity, saleStatus, deliveryFee);
		}
	}
}
