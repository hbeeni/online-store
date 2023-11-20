package com.been.onlinestore.service;

import com.been.onlinestore.common.ErrorMessages;
import com.been.onlinestore.domain.Cart;
import com.been.onlinestore.domain.CartProduct;
import com.been.onlinestore.domain.Product;
import com.been.onlinestore.repository.CartProductRepository;
import com.been.onlinestore.repository.CartRepository;
import com.been.onlinestore.repository.ProductRepository;
import com.been.onlinestore.service.request.CartProductServiceRequest;
import com.been.onlinestore.service.response.CartProductResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.Optional;
import java.util.Set;

@RequiredArgsConstructor
@Transactional
@Service
public class CartProductService {

    private final CartProductRepository cartProductRepository;
    private final ProductRepository productRepository;
    private final CartRepository cartRepository;

    protected CartProduct addCartProduct(Cart cart, CartProductServiceRequest.Create serviceRequest) {
        Optional<CartProduct> cartProductOptional = cartProductRepository.findByCart_IdAndProduct_Id(cart.getId(), serviceRequest.productId());

        if (cartProductOptional.isPresent()) {
            CartProduct cartProduct = cartProductOptional.get();
            cartProduct.updateProductQuantity(cartProduct.getProductQuantity() + serviceRequest.productQuantity());
            return cartProduct;
        }  else {
            Product product = productRepository.getReferenceById(serviceRequest.productId());
            return cartProductRepository.save(serviceRequest.toEntity(cart, product));
        }
    }

    public CartProductResponse updateCartProductQuantity(Long cartProductId, Long userId, int updateProductQuantity) {
        CartProduct cartProduct = cartProductRepository.findCartProduct(cartProductId, userId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorMessages.NOT_FOUND_CART_PRODUCT.getMessage()));
        cartProduct.updateProductQuantity(updateProductQuantity);
        return CartProductResponse.from(cartProduct);
    }

    public void deleteCartProducts(Set<Long> cartProductIds, Long userId) {
        Cart cart = cartRepository.findFirstByUser_IdOrderByCreatedAtDesc(userId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorMessages.NOT_FOUND_CART.getMessage()));
        cartProductRepository.deleteCartProducts(cartProductIds, cart.getId());
        if (cart.getCartProducts().isEmpty()) {
            cartRepository.delete(cart);
        }
    }

    protected void deleteCartProductsInCart(Set<Long> ids) {
        cartProductRepository.deleteAllByIdInBatch(ids);
    }
}
