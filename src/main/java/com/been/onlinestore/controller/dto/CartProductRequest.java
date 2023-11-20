package com.been.onlinestore.controller.dto;

import com.been.onlinestore.service.request.CartProductServiceRequest;
import lombok.Builder;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public record CartProductRequest() {

    @Builder
    public record Create(
            @NotNull @Min(1)
            Long productId,

            @NotNull @Min(1)
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
            @NotNull @Min(1)
            Integer productQuantity
    ) {}
}
