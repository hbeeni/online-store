package com.been.onlinestore.controller.dto;

import lombok.Builder;

public record CartProductRequest() {

    @Builder
    public record Create(
            Long productId,
            Integer productQuantity
    ) {}

    @Builder
    public record Update(
            Integer productQuantity
    ) {}
}
