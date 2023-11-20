package com.been.onlinestore.repository;

import com.been.onlinestore.domain.CartProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.Optional;

public interface CartProductRepository extends JpaRepository<CartProduct, Long> {

    Optional<CartProduct> findByCart_IdAndProduct_Id(Long cartId, Long cartProductId);

    @Query("select cp from CartProduct cp " +
            "join fetch cp.product p " +
            "join cp.cart c " +
            "where cp.id = :cartProductId and c.user.id = :userId")
    Optional<CartProduct> findCartProduct(@Param("cartProductId") Long cartProductId, @Param("userId") Long userId);

    @Modifying
    @Query("delete from CartProduct cp where cp.id in :cartProductIds and cp.cart.id = :cartId")
    void deleteCartProducts(@Param("cartProductIds") Collection<Long> cartProductIds, @Param("cartId") Long cartId);
}
