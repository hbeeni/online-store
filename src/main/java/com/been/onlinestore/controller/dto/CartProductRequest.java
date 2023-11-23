package com.been.onlinestore.controller.dto;

import com.been.onlinestore.service.request.CartProductServiceRequest;
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
    ) {

        public CartProductServiceRequest.Create toServiceRequest() {
            return CartProductServiceRequest.Create.of(
                    productId,
                    productQuantity
            );
        }
    }

    @Builder
    public record Update(
            @NotNull @PositiveOrZero
            Integer productQuantity
    ) {}
}
