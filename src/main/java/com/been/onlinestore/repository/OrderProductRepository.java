package com.been.onlinestore.repository;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.been.onlinestore.domain.OrderProduct;

public interface OrderProductRepository extends JpaRepository<OrderProduct, Long> {

	@Query("select op from OrderProduct op "
		+ "join fetch op.delivery d "
		+ "join fetch op.product p "
		+ "where op.id in :orderProductIds and p.seller.id = :sellerId")
	List<OrderProduct> findAllByIdAndSellerId(@Param("orderProductIds") Set<Long> orderProductIds,
		@Param("sellerId") Long sellerId);

	@Modifying
	@Query(nativeQuery = true,
		value = "update order_product op "
			+ "join delivery d on d.id = op.delivery_id "
			+ "set d.delivery_status = 'PREPARING' "
			+ "where op.id in :orderProductIds")
	int bulkStartPreparing(@Param("orderProductIds") Collection<Long> orderProductIds);

	@Modifying
	@Query(nativeQuery = true,
		value = "update order_product op "
			+ "join delivery d on d.id = op.delivery_id "
			+ "set d.delivery_status = 'DELIVERING' "
			+ "where op.id in :orderProductIds")
	int bulkStartDelivery(@Param("orderProductIds") Collection<Long> orderProductIds);

	@Modifying
	@Query(nativeQuery = true,
		value = "update order_product op "
			+ "join delivery d on d.id = op.delivery_id "
			+ "set d.delivery_status = 'FINAL_DELIVERY', d.delivered_at = now() "
			+ "where op.id in :orderProductIds")
	int bulkCompleteDelivery(@Param("orderProductIds") Collection<Long> orderProductIds);
}
