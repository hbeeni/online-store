package com.been.onlinestore.domain;

import com.been.onlinestore.domain.constant.DeliveryStatus;

import java.time.LocalDateTime;

public class Delivery {

    private Long id;

    private DeliveryStatus deliveryStatus;
    private String deliveryAddress;
    private int deliveryFee;
    private String phone;
    private LocalDateTime deliveredAt;
}
