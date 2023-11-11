package com.been.onlinestore.service;

import com.been.onlinestore.domain.Cart;
import com.been.onlinestore.domain.CartProduct;
import com.been.onlinestore.domain.Product;
import com.been.onlinestore.dto.CartProductDto;
import com.been.onlinestore.repository.CartProductRepository;
import com.been.onlinestore.repository.CartRepository;
import com.been.onlinestore.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class CartProductService {

    private final CartProductRepository cartProductRepository;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    public CartProductDto findCartProduct(Long id) {
        return cartProductRepository.findById(id)
                .map(CartProductDto::from)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 장바구니 상품입니다."));
    }

    protected CartProductDto addCartProduct(Cart cart, CartProductDto dto) {
        Long productId = dto.productDto().id();
        Product product = productRepository.getReferenceById(productId);

        CartProduct cartProduct = cartProductRepository.findByCart_IdAndProduct_Id(cart.getId(), productId)
                .orElseGet(() -> cartProductRepository.save(dto.toEntity(cart, product)));
        cartProduct.updateProductQuantity(dto.productQuantity());

        return CartProductDto.from(cartProduct);
    }

    public CartProductDto updateCartProductQuantity(Long cartProductId, Long cartId, Long userId, int updateProductQuantity) {
        validateCartBelongsToUser(cartId, userId);

        CartProduct cartProduct = cartProductRepository.findByIdAndCart_Id(cartProductId, cartId)
                .orElseThrow(() -> new IllegalArgumentException("장바구니에 해당 상품이 존재하지 않습니다. 장바구니 상품 ID = " + cartProductId));

        cartProduct.updateProductQuantity(updateProductQuantity);
        return CartProductDto.from(cartProduct);
    }

    public Long deleteCartProduct(Long cartProductId, Long cartId, Long userId) {
        validateCartBelongsToUser(cartId, userId);
        cartProductRepository.deleteByIdAndCart_Id(cartProductId, cartId);
        return cartProductId;
    }

    private void validateCartBelongsToUser(Long cartId, Long userId) {
        cartRepository.findByIdAndUser_Id(cartId, userId)
                .orElseThrow(() -> new IllegalArgumentException("본인의 장바구니의 상품만 수정할 수 있습니다."));
    }
}
