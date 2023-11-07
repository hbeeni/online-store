package com.been.onlinestore.repository;

import com.been.onlinestore.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @Modifying
    @Query("update Product p set p.category = null where p.category.id = :categoryId")
    int bulkCategoryNull(@Param("categoryId") Long categoryId);
}
