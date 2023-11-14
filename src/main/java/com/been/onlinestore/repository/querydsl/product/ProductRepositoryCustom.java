package com.been.onlinestore.repository.querydsl.product;

import com.been.onlinestore.domain.Product;
import com.been.onlinestore.dto.ProductSearchCondition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductRepositoryCustom {
    Page<Product> searchProducts(ProductSearchCondition cond, Pageable pageable);

    Page<Product> searchProductsBySellerId(Long sellerId, ProductSearchCondition cond, Pageable pageable);
}
