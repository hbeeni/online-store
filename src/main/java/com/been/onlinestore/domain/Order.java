package com.been.onlinestore.domain;

import com.been.onlinestore.domain.constant.OrderStatus;

import java.time.LocalDateTime;

public class Order {

    private Long id;

    private User user;
    private Delivery delivery;

    private String phone;
    private OrderStatus orderStatus;

    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
}
