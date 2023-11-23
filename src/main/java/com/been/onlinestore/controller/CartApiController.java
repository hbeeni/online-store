package com.been.onlinestore.controller;

import com.been.onlinestore.controller.dto.ApiResponse;
import com.been.onlinestore.controller.dto.CartProductRequest;
import com.been.onlinestore.controller.dto.security.PrincipalDetails;
import com.been.onlinestore.service.CartProductService;
import com.been.onlinestore.service.CartService;
import com.been.onlinestore.service.response.CartIdAndCartProductIdResponse;
import com.been.onlinestore.service.response.CartProductResponse;
import com.been.onlinestore.service.response.CartWithCartProductsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

@RequiredArgsConstructor
@RequestMapping("/api/carts")
@RestController
public class CartApiController {

    private final CartService cartService;
    private final CartProductService cartProductService;

    @GetMapping
    public ResponseEntity<ApiResponse<?>> getProductsInCart(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        Optional<CartWithCartProductsResponse> responseOptional = cartService.findCart(principalDetails.id());
        if (responseOptional.isEmpty()) {
            return ResponseEntity.ok(ApiResponse.success(Collections.emptyList()));
        }
        return ResponseEntity.ok(ApiResponse.success(responseOptional.get()));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CartIdAndCartProductIdResponse>> addProductToCart(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @RequestBody @Validated CartProductRequest.Create request
    ) {
        return ResponseEntity.ok(ApiResponse.success(cartService.addCartProductToCart(principalDetails.id(), request.toServiceRequest())));
    }

    @PutMapping("/products/{cartProductId}")
    public ResponseEntity<ApiResponse<CartProductResponse>> updateProductInCart(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @PathVariable Long cartProductId,
            @RequestBody @Validated CartProductRequest.Update request
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                cartProductService.updateCartProductQuantity(cartProductId, principalDetails.id(), request.productQuantity()))
        );
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> deleteCart(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        cartService.deleteCart(principalDetails.id());
        return ResponseEntity.ok(ApiResponse.success());
    }

    @DeleteMapping("/products")
    public ResponseEntity<ApiResponse<Void>> deleteCartProducts(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @RequestParam Set<Long> ids
    ) {
        cartProductService.deleteCartProducts(ids, principalDetails.id());
        return ResponseEntity.ok(ApiResponse.success());
    }
}
