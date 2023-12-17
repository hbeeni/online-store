package com.been.onlinestore.controller.admin;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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

import com.been.onlinestore.controller.dto.ApiResponse;
import com.been.onlinestore.controller.dto.ProductRequest;
import com.been.onlinestore.file.ImageStore;
import com.been.onlinestore.repository.querydsl.product.AdminProductResponse;
import com.been.onlinestore.repository.querydsl.product.ProductSearchCondition;
import com.been.onlinestore.service.admin.AdminProductService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/api/admin/products")
@RestController
public class AdminProductApiController {

	private final AdminProductService adminProductService;
	private final ImageStore imageStore;

	@GetMapping
	public ResponseEntity<ApiResponse<List<AdminProductResponse>>> getProducts(
		@ModelAttribute @Validated ProductSearchCondition cond,
		@PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
	) {
		return ResponseEntity.ok(ApiResponse.pagination(adminProductService.findProducts(cond, pageable)));
	}

	@GetMapping("/{productId}")
	public ResponseEntity<ApiResponse<AdminProductResponse>> getProduct(@PathVariable Long productId) {
		return ResponseEntity.ok(ApiResponse.success(adminProductService.findProduct(productId)));
	}

	@PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
	public ResponseEntity<ApiResponse<Map<String, Long>>> addProduct(
		@RequestPart @Validated ProductRequest.Create request,
		@RequestPart MultipartFile image
	) throws IOException {
		String savedImageName = imageStore.saveImage(image);
		return ResponseEntity.ok(ApiResponse.successId(
			adminProductService.addProduct(request.toServiceRequest(), savedImageName)));
	}

	@PutMapping("/{productId}")
	public ResponseEntity<ApiResponse<Map<String, Long>>> updateProductInfo(
		@PathVariable Long productId,
		@RequestBody @Validated ProductRequest.Update request
	) {
		return ResponseEntity.ok(ApiResponse.successId(
			adminProductService.updateProductInfo(productId, request.toServiceRequest())));
	}

	@PutMapping("/{productId}/img")
	public ResponseEntity<ApiResponse<Map<String, Long>>> updateProductImage(
		@PathVariable Long productId,
		@RequestPart MultipartFile image
	) throws IOException {
		String savedImageName = imageStore.saveImage(image);
		return ResponseEntity.ok(ApiResponse.successId(
			adminProductService.updateProductImage(productId, savedImageName)));
	}
}
