package com.been.onlinestore.service;

import com.been.onlinestore.domain.Cart;
import com.been.onlinestore.domain.CartProduct;
import com.been.onlinestore.domain.User;
import com.been.onlinestore.repository.CartRepository;
import com.been.onlinestore.repository.UserRepository;
import com.been.onlinestore.service.request.CartProductServiceRequest;
import com.been.onlinestore.service.response.CartIdAndCartProductIdResponse;
import com.been.onlinestore.service.response.CartWithCartProductsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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

    public CartIdAndCartProductIdResponse addCartProductToCart(Long userId, CartProductServiceRequest.Create serviceRequest) {
        Cart cart = cartRepository.findFirstByUser_IdOrderByCreatedAtDesc(userId)
                .orElseGet(() -> {
                    User user = userRepository.getReferenceById(userId);
                    return cartRepository.save(Cart.of(user));
                });

        CartProduct cartProduct = cartProductService.addCartProduct(cart, serviceRequest);
        return CartIdAndCartProductIdResponse.of(cart.getId(), cartProduct.getId());
    }

    public void deleteCart(Long userId) {
        Optional<Cart> cartOptional = cartRepository.findFirstByUser_IdOrderByCreatedAtDesc(userId);
        if (cartOptional.isPresent()) {
            Cart cart = cartOptional.get();
            cartProductService.deleteCartProductsInCart(getCartProductsSet(cart));
            cartRepository.delete(cart);
        }
    }

    private Set<Long> getCartProductsSet(Cart cart) {
        return cart.getCartProducts().stream()
                .map(CartProduct::getId)
                .collect(Collectors.toSet());
    }
}
