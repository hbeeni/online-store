package com.been.onlinestore.repository;

import com.been.onlinestore.domain.Order;
import com.been.onlinestore.repository.querydsl.order.OrderRepositoryCustom;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long>, OrderRepositoryCustom {

	@Query("select o from Order o " +
			"join fetch o.orderProducts op " +
			"join fetch op.product p " +
			"where o.id = :orderId and o.orderer.id = :ordererId")
	Optional<Order> findByIdAndOrdererId(@Param("orderId") Long orderId, @Param("ordererId") Long ordererId);
}
