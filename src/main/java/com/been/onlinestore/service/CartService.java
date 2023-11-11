package com.been.onlinestore.service;

import com.been.onlinestore.domain.Cart;
import com.been.onlinestore.domain.User;
import com.been.onlinestore.dto.CartProductDto;
import com.been.onlinestore.dto.CartWithCartProductsDto;
import com.been.onlinestore.repository.CartRepository;
import com.been.onlinestore.repository.UserRepository;
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
    public Optional<CartWithCartProductsDto> findCart(Long userId) {
        return cartRepository.findByUser_Id(userId)
                .map(CartWithCartProductsDto::from);
    }

    public Map<String, Long> addCartProductToCart(Long userId, CartProductDto cartProductDto) {
        Cart cart = getCartByUser(userId);
        CartProductDto savedCartProductDto = cartProductService.addCartProduct(cart, cartProductDto);
        return makeCartIdAndCartProductIdMap(cart.getId(), savedCartProductDto.id());
    }

    public Long deleteCart(Long cartId, Long userId) {
        if (cartRepository.existsByIdAndUser_Id(cartId, userId)) {
            cartProductService.deleteCartProducts(cartId);
            cartRepository.deleteById(cartId);
            return cartId;
        } else {
            throw new IllegalArgumentException("본인의 장바구니만 삭제할 수 있습니다.");
        }
    }

    private Cart getCartByUser(Long userId) {
        return cartRepository.findByUser_Id(userId)
                .orElseGet(() -> {
                            User user = userRepository.getReferenceById(userId);
                            return cartRepository.save(Cart.of(user));
                        }
                );
    }

    private Map<String, Long> makeCartIdAndCartProductIdMap(Long cartId, Long cartProductId) {
        Map<String, Long> map = new HashMap<>();
        map.put("cartId", cartId);
        map.put("cartProductId", cartProductId);
        return map;
    }
}
