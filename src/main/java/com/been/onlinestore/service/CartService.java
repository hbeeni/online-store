package com.been.onlinestore.service;

import com.been.onlinestore.domain.Cart;
import com.been.onlinestore.domain.CartProduct;
import com.been.onlinestore.domain.User;
import com.been.onlinestore.repository.CartRepository;
import com.been.onlinestore.repository.UserRepository;
import com.been.onlinestore.service.request.CartProductServiceRequest;
import com.been.onlinestore.service.response.CartWithCartProductsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@Transactional
@Service
public class CartService {

    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final CartProductService cartProductService;

    @Transactional(readOnly = true)
    public Optional<CartWithCartProductsResponse> findCart(Long userId) {
        return cartRepository.findFirstByUser_IdOrderByCreatedAtDesc(userId)
                .map(CartWithCartProductsResponse::from);
    }

    public Map<String, Long> addCartProductToCart(Long userId, CartProductServiceRequest.Create serviceRequest) {
        Cart cart;
        boolean isCartExist = false;

        Optional<Cart> cartOptional = cartRepository.findFirstByUser_IdOrderByCreatedAtDesc(userId);
        if (cartOptional.isPresent()) {
            cart = cartOptional.get();
            isCartExist = true;
        } else {
            User user = userRepository.getReferenceById(userId);
            cart = Cart.of(user);
        }

        CartProduct cartProduct = cartProductService.addCartProduct(cart, serviceRequest);
        if (!isCartExist) {
            cartRepository.save(cart);
        }

        return makeCartIdAndCartProductIdMap(cart.getId(), cartProduct.getId());
    }

    public Long deleteCart(Long cartId, Long userId) {
        cartRepository.deleteByIdAndUser_Id(cartId, userId);
        return cartId;
    }

    private Map<String, Long> makeCartIdAndCartProductIdMap(Long cartId, Long cartProductId) {
        Map<String, Long> map = new HashMap<>();
        map.put("cartId", cartId);
        map.put("cartProductId", cartProductId);
        return map;
    }
}
