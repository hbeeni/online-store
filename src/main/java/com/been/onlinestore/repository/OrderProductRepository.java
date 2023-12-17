package com.been.onlinestore.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.been.onlinestore.domain.OrderProduct;

public interface OrderProductRepository extends JpaRepository<OrderProduct, Long> {
}
