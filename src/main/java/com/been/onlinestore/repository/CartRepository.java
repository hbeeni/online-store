package com.been.onlinestore.repository;

import com.been.onlinestore.domain.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {

    boolean existsByIdAndUser_Id(Long cartId, Long userId);

    Optional<Cart> findByUser_Id(Long userId);
}
