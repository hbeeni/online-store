package com.been.onlinestore.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.been.onlinestore.domain.Delivery;

public interface DeliveryRepository extends JpaRepository<Delivery, Long> {
}
