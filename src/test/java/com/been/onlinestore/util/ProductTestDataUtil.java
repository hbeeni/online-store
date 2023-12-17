package com.been.onlinestore.util;

import static com.been.onlinestore.util.CategoryTestDataUtil.*;
import static java.time.LocalDateTime.*;

import org.springframework.test.util.ReflectionTestUtils;

import com.been.onlinestore.domain.Product;
import com.been.onlinestore.domain.constant.SaleStatus;
import com.been.onlinestore.repository.querydsl.product.AdminProductResponse;

public class ProductTestDataUtil {

	public static Product createProduct() {
		return createProduct(1L);
	}

	public static Product createProduct(Long id) {
		Product product = Product.of(
			createCategory(1L, "category"),
			"name",
			10000,
			"description",
			1000,
			0,
			SaleStatus.SALE,
			3000,
			"image.png"
		);
		ReflectionTestUtils.setField(product, "id", id);
		return product;
	}

	public static AdminProductResponse createAdminProductResponse(Long id) {
		return createAdminProductResponse(id, "name", "category");
	}

	public static AdminProductResponse createAdminProductResponse(Long id, String productName, String categoryName) {
		return AdminProductResponse.of(
			id,
			categoryName,
			productName,
			1000,
			"des",
			100,
			100,
			SaleStatus.SALE,
			3000,
			null,
			now(),
			"been",
			now(),
			"been"
		);
	}
}
