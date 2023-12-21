package com.been.onlinestore.controller;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.been.onlinestore.common.ApiResponse;
import com.been.onlinestore.file.ImageStore;
import com.been.onlinestore.service.ProductService;
import com.been.onlinestore.service.dto.response.CategoryProductResponse;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/api/products")
@RestController
public class ProductApiController {

	private final ProductService productService;
	private final ImageStore imageStore;

	@GetMapping
	public ResponseEntity<ApiResponse<List<CategoryProductResponse>>> getProducts(
		@RequestParam(required = false) String searchName,
		@PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
	) {
		return ResponseEntity.ok(
			ApiResponse.pagination(productService.findProductsOnSale(searchName, pageable)));
	}

	@GetMapping("/{productId}")
	public ResponseEntity<ApiResponse<CategoryProductResponse>> getProduct(@PathVariable Long productId) {
		return ResponseEntity.ok(ApiResponse.success(productService.findProductOnSale(productId)));
	}

	@GetMapping(value = "/img/{imageName}", produces = {MediaType.IMAGE_PNG_VALUE, MediaType.IMAGE_JPEG_VALUE})
	public ResponseEntity<byte[]> getProductImage(@PathVariable String imageName) {
		return ResponseEntity.ok(imageStore.getProductImage(imageName));
	}
}
