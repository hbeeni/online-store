package com.been.onlinestore.controller.seller;

import com.been.onlinestore.controller.dto.ApiResponse;
import com.been.onlinestore.controller.dto.ProductRequest;
import com.been.onlinestore.controller.dto.security.PrincipalDetails;
import com.been.onlinestore.file.ImageStore;
import com.been.onlinestore.repository.querydsl.product.AdminProductResponse;
import com.been.onlinestore.repository.querydsl.product.ProductSearchCondition;
import com.been.onlinestore.service.ProductService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/seller/products")
@RestController
public class SellerProductApiController {

	private final ProductService productService;
	private final ImageStore imageStore;

	@GetMapping
	public ResponseEntity<ApiResponse<List<AdminProductResponse>>> getProducts(
			@AuthenticationPrincipal PrincipalDetails principalDetails,
			@ModelAttribute @Validated ProductSearchCondition cond,
			@PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
	) {
		return ResponseEntity.ok(
				ApiResponse.pagination(productService.findProductsForSeller(principalDetails.id(), cond, pageable)));
	}

	@GetMapping("/{productId}")
	public ResponseEntity<ApiResponse<AdminProductResponse>> getProduct(
			@AuthenticationPrincipal PrincipalDetails principalDetails, @PathVariable Long productId) {
		return ResponseEntity.ok(
				ApiResponse.success(productService.findProductForSeller(productId, principalDetails.id())));
	}

	@PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
	public ResponseEntity<ApiResponse<Map<String, Long>>> addProduct(
			@AuthenticationPrincipal PrincipalDetails principalDetails,
			@RequestPart @Validated ProductRequest.Create request,
			@RequestPart MultipartFile image
	) throws IOException {
		String savedImageName = imageStore.saveImage(image);
		return ResponseEntity.ok(ApiResponse.successId(
				productService.addProduct(principalDetails.id(), request.toServiceRequest(), savedImageName)));
	}

	@PutMapping("/{productId}")
	public ResponseEntity<ApiResponse<Map<String, Long>>> updateProductInfo(
			@AuthenticationPrincipal PrincipalDetails principalDetails,
			@PathVariable Long productId,
			@RequestBody @Validated ProductRequest.Update request
	) {
		return ResponseEntity.ok(ApiResponse.successId(
				productService.updateProductInfo(productId, principalDetails.id(), request.toServiceRequest())));
	}

	@PutMapping("/{productId}/img")
	public ResponseEntity<ApiResponse<Map<String, Long>>> updateProductImage(
			@AuthenticationPrincipal PrincipalDetails principalDetails,
			@PathVariable Long productId,
			@RequestPart MultipartFile image
	) throws IOException {
		String savedImageName = imageStore.saveImage(image);
		return ResponseEntity.ok(ApiResponse.successId(
				productService.updateProductImage(productId, principalDetails.id(), savedImageName)));
	}
}
