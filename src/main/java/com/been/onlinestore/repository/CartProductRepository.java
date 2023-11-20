package com.been.onlinestore.repository;

import com.been.onlinestore.domain.CartProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CartProductRepository extends JpaRepository<CartProduct, Long> {

    Optional<CartProduct> findByCart_IdAndProduct_Id(Long cartId, Long cartProductId);

    @Query("select cp from CartProduct cp join fetch Product p where cp.id = :cartProductId and cp.cart.id = :cartId and cp.cart.user.id = :userId")
    Optional<CartProduct> findCartProduct(@Param("cartProductId") Long cartProductId, @Param("cartId") Long cartId, @Param("userId") Long userId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("delete CartProduct cp where cp.id in :cartProductIds and cp.cart.id = :cartId and cp.product.id = :productId")
    void deleteCartProducts(@Param("cartProductId") List<Long> cartProductIds, @Param("cartId") Long cartId, @Param("productId") Long userId);
}
