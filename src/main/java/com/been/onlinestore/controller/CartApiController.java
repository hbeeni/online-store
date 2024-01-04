package com.been.onlinestore.controller;

import java.util.List;
import java.util.Map;

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

import com.been.onlinestore.common.ApiResponse;
import com.been.onlinestore.controller.dto.CartProductRequest;
import com.been.onlinestore.controller.dto.security.PrincipalDetails;
import com.been.onlinestore.service.CartProductService;
import com.been.onlinestore.service.dto.response.CartProductResponse;
import com.been.onlinestore.service.dto.response.CartResponse;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/api/carts")
@RestController
public class CartApiController {

	private final CartProductService cartProductService;

	@GetMapping
	public ResponseEntity<ApiResponse<CartResponse>> getProductsInCart(
		@AuthenticationPrincipal PrincipalDetails principalDetails
	) {
		return ResponseEntity.ok(ApiResponse.success(cartProductService.findCartProducts(principalDetails.id())));
	}

	@PostMapping
	public ResponseEntity<ApiResponse<CartProductResponse>> addProductToCart(
		@AuthenticationPrincipal PrincipalDetails principalDetails,
		@RequestBody @Validated CartProductRequest.Create request
	) {
		return ResponseEntity.ok(ApiResponse.success(
			cartProductService.addCartProduct(principalDetails.id(), request.toServiceRequest())
		));
	}

	@PostMapping("/order")
	public ResponseEntity<ApiResponse<Map<String, Long>>> orderCartProducts(
		@AuthenticationPrincipal PrincipalDetails principalDetails,
		@RequestBody @Validated CartProductRequest.Order request
	) {
		return ResponseEntity.ok(ApiResponse.successId(
			cartProductService.order(principalDetails.id(), request.toServiceRequest())
		));
	}

	@PutMapping("/products/{cartProductId}")
	public ResponseEntity<ApiResponse<CartProductResponse>> updateProductInCart(
		@AuthenticationPrincipal PrincipalDetails principalDetails,
		@PathVariable Long cartProductId,
		@RequestBody @Validated CartProductRequest.Update request
	) {
		return ResponseEntity.ok(ApiResponse.success(cartProductService.updateCartProductQuantity(
			principalDetails.id(), cartProductId, request.productQuantity()
		)));
	}

	@DeleteMapping("/products")
	public ResponseEntity<ApiResponse<Void>> deleteCartProducts(
		@AuthenticationPrincipal PrincipalDetails principalDetails,
		@RequestParam List<Long> ids
	) {
		cartProductService.deleteCartProducts(principalDetails.id(), ids);
		return ResponseEntity.ok(ApiResponse.success());
	}
}
