package com.been.onlinestore.repository.querydsl.product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface ProductRepositoryCustom {
	Page<AdminProductResponse> searchProducts(Long sellerId, ProductSearchCondition cond, Pageable pageable);

	Optional<AdminProductResponse> searchProduct(Long productId, Long sellerId);
}
