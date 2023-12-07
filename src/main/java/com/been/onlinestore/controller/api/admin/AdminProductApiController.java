package com.been.onlinestore.controller.api.admin;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.been.onlinestore.controller.dto.ApiResponse;
import com.been.onlinestore.repository.querydsl.product.AdminProductResponse;
import com.been.onlinestore.repository.querydsl.product.ProductSearchCondition;
import com.been.onlinestore.service.ProductService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/api/admin/products")
@RestController
public class AdminProductApiController {

	private final ProductService productService;

	@GetMapping
	public ResponseEntity<ApiResponse<List<AdminProductResponse>>> getProducts(
		@ModelAttribute @Validated ProductSearchCondition cond,
		@PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
	) {
		return ResponseEntity.ok(ApiResponse.pagination(productService.findProductsForAdmin(cond, pageable)));
	}

	@GetMapping("/{productId}")
	public ResponseEntity<ApiResponse<AdminProductResponse>> getProduct(@PathVariable Long productId) {
		return ResponseEntity.ok(ApiResponse.success(productService.findProductForAdmin(productId)));
	}
}
