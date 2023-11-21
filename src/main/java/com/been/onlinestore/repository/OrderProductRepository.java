package com.been.onlinestore.repository;

import com.been.onlinestore.domain.OrderProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface OrderProductRepository extends JpaRepository<OrderProduct, Long> {

    @Query("select op from OrderProduct op " +
            "join fetch op.delivery d " +
            "join fetch op.product p " +
            "where op.id in :orderProductIds and p.seller.id = :sellerId")
    List<OrderProduct> findAllByIdAndSellerId(@Param("orderProductIds") Set<Long> orderProductIds, @Param("sellerId") Long sellerId);
}
