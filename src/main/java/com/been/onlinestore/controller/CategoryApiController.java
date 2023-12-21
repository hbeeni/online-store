package com.been.onlinestore.controller;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.been.onlinestore.common.ApiResponse;
import com.been.onlinestore.service.CategoryService;
import com.been.onlinestore.service.ProductService;
import com.been.onlinestore.service.dto.response.CategoryResponse;
import com.been.onlinestore.service.dto.response.ProductResponse;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/api/categories")
@RestController
public class CategoryApiController {

	private final CategoryService categoryService;
	private final ProductService productService;

	@GetMapping
	public ResponseEntity<ApiResponse<List<CategoryResponse>>> getCategories() {
		return ResponseEntity.ok(ApiResponse.success(categoryService.findCategories()));
	}

	@GetMapping("/{categoryId}")
	public ResponseEntity<ApiResponse<List<ProductResponse>>> getAllProductsInCategory(@PathVariable Long categoryId,
		@PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
		return ResponseEntity.ok(
			ApiResponse.pagination(productService.findProductsInCategory(categoryId, pageable)));
	}
}
