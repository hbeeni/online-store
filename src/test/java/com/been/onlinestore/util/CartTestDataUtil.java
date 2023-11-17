package com.been.onlinestore.util;

import com.been.onlinestore.domain.Cart;
import com.been.onlinestore.domain.CartProduct;
import com.been.onlinestore.dto.CartProductDto;
import org.springframework.test.util.ReflectionTestUtils;

import static com.been.onlinestore.util.ProductTestDataUtil.createProduct;
import static com.been.onlinestore.util.UserTestDataUtil.createUser;

public class CartTestDataUtil {

    public static Cart createCart(Long cartId, Long userId) {
        Cart cart = Cart.of(createUser(userId));
        ReflectionTestUtils.setField(cart, "id", cartId);
        return cart;
    }

    public static CartProduct createCartProduct(Long cartProductId, Long cartId, Long productId) {
        CartProduct cartProduct = CartProduct.of(createCart(cartId, 1L), createProduct(productId), 10);
        ReflectionTestUtils.setField(cartProduct, "id", cartProductId);
        return cartProduct;
    }

    public static CartProductDto createCartProductDto(Long cartProductId, Long cartId) {
        return CartProductDto.of(
                cartProductId,
                cartId,
                null,
                10
        );
    }
}
