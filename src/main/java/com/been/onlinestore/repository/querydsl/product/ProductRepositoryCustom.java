package com.been.onlinestore.repository.querydsl.product;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductRepositoryCustom {
	Page<AdminProductResponse> searchProducts(Long sellerId, ProductSearchCondition cond, Pageable pageable);

	Optional<AdminProductResponse> searchProduct(Long productId, Long sellerId);
}
