package com.been.onlinestore.service.response;

public record CartIdAndCartProductIdResponse(Long cartId, Long cartProductId) {

    public static CartIdAndCartProductIdResponse of(Long cartId, Long cartProductId) {
        return new CartIdAndCartProductIdResponse(cartId, cartProductId);
    }
}
