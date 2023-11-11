package com.been.onlinestore.repository;

import com.been.onlinestore.domain.CartProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CartProductRepository extends JpaRepository<CartProduct, Long> {

    boolean existsByCart_Id(Long cartId);

    Optional<CartProduct> findByIdAndCart_Id(Long cartProductId, Long cartId);
    Optional<CartProduct> findByCart_IdAndProduct_Id(Long cartId, Long cartProductId);

    void deleteByIdAndCart_Id(Long cartProductId, Long cartId);

    @Modifying
    @Query("delete from CartProduct cp where cp.cart.id = :cartId")
    void deleteByCartId(@Param("cartId") Long cartId);
}
