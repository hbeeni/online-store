package com.been.onlinestore.repository;

import com.been.onlinestore.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
