package com.been.onlinestore.domain;

import com.been.onlinestore.domain.constant.SaleStatus;

import java.time.LocalDateTime;

public class Product {

    private Long id;

    private Category category;

    private String name;
    private int price;
    private String description;
    private int stockQuantity;
    private int salesVolume;
    private SaleStatus saleStatus;
    private String imageUrl;

    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime modifiedAt;
    private String modifiedBy;
}
