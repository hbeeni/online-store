package com.been.onlinestore.service;

import com.been.onlinestore.common.ErrorMessages;
import com.been.onlinestore.domain.Cart;
import com.been.onlinestore.domain.CartProduct;
import com.been.onlinestore.domain.Product;
import com.been.onlinestore.repository.CartProductRepository;
import com.been.onlinestore.repository.ProductRepository;
import com.been.onlinestore.service.request.CartProductServiceRequest;
import com.been.onlinestore.service.response.CartProductResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Transactional
@Service
public class CartProductService {

    private final CartProductRepository cartProductRepository;
    private final ProductRepository productRepository;

    protected CartProduct addCartProduct(Cart cart, CartProductServiceRequest.Create serviceRequest) {
        Product product = productRepository.getReferenceById(serviceRequest.productId());
        Optional<CartProduct> cartProductOptional = cartProductRepository.findByCart_IdAndProduct_Id(cart.getId(), serviceRequest.productId());

        CartProduct cartProduct;
        if (cartProductOptional.isPresent()) {
            cartProduct = cartProductOptional.get();
            cartProduct.updateProductQuantity(serviceRequest.productQuantity());
        }  else {
            cartProduct = serviceRequest.toEntity(cart, product);
        }

        cart.addCartProduct(cartProduct);
        return cartProduct;
    }

    public CartProductResponse updateCartProductQuantity(Long cartProductId, Long cartId, Long userId, int updateProductQuantity) {
        CartProduct cartProduct = cartProductRepository.findCartProduct(cartProductId, cartId, userId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorMessages.NOT_FOUND_CART_PRODUCT.getMessage()));
        cartProduct.updateProductQuantity(updateProductQuantity);
        return CartProductResponse.from(cartProduct);
    }

    //TODO: 모든 장바구니 상품을 삭제하면 장바구니도 삭제해야 하는데 어떻게 할건지
    public IdsMap deleteCartProducts(List<Long> cartProductIds, Long cartId, Long userId) {
        cartProductRepository.deleteCartProducts(cartProductIds, cartId, userId);
        return makeCartIdAndCartProductIdsMap(cartId, cartProductIds);
    }

    private IdsMap makeCartIdAndCartProductIdsMap(Long cartId, List<Long> cartProductIds) {
        return new IdsMap(cartId, cartProductIds);
    }

    public record IdsMap(Long cartId, List<Long> cartProductId) {}
}
