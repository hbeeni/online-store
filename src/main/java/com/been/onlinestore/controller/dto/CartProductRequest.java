package com.been.onlinestore.controller.dto;

import lombok.Builder;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

public record CartProductRequest() {

    @Builder
    public record Create(
            @NotNull @Positive
            Long productId,

            @NotNull @PositiveOrZero
            Integer productQuantity
    ) {}

    @Builder
    public record Update(
            @NotNull @PositiveOrZero
            Integer productQuantity
    ) {}
}
