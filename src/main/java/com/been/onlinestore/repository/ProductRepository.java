package com.been.onlinestore.repository;

import com.been.onlinestore.domain.Product;
import com.been.onlinestore.domain.constant.SaleStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {

    int countByCategory_IdAndSaleStatusEquals(Long categoryId, SaleStatus saleStatus);
}
