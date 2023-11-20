package com.been.onlinestore.repository;

import com.been.onlinestore.domain.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {

    Optional<Cart> findFirstByUser_IdOrderByCreatedAtDesc(Long userId);

    void deleteByIdAndUser_Id(Long cartId, Long userId);
}
