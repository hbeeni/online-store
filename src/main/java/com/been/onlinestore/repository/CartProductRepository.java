package com.been.onlinestore.repository;

import com.been.onlinestore.domain.CartProduct;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartProductRepository extends JpaRepository<CartProduct, Long> {

    Optional<CartProduct> findByIdAndCart_Id(Long cartProductId, Long cartId);
    void deleteByIdAndCart_Id(Long cartProductId, Long cartId);
}
