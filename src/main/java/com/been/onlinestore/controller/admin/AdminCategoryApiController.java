package com.been.onlinestore.controller.admin;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.been.onlinestore.controller.dto.CategoryRequest;
import com.been.onlinestore.response.ApiResponse;
import com.been.onlinestore.service.admin.AdminCategoryService;
import com.been.onlinestore.service.dto.response.admin.AdminCategoryResponse;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/api/admin/categories")
@RestController
public class AdminCategoryApiController {

	private final AdminCategoryService adminCategoryService;

	@GetMapping
	public ResponseEntity<ApiResponse<List<AdminCategoryResponse>>> getCategories() {
		return ResponseEntity.ok(ApiResponse.success(adminCategoryService.findCategories()));
	}

	@GetMapping("/{categoryId}")
	public ResponseEntity<ApiResponse<AdminCategoryResponse>> getCategory(@PathVariable Long categoryId) {
		return ResponseEntity.ok(ApiResponse.success(adminCategoryService.findCategory(categoryId)));
	}

	@PostMapping
	public ResponseEntity<ApiResponse<Map<String, Long>>> addCategory(
		@RequestBody @Validated CategoryRequest.Create request) {
		return ResponseEntity.ok(ApiResponse.successId(adminCategoryService.addCategory(request.toServiceRequest())));
	}

	@PutMapping("/{categoryId}")
	public ResponseEntity<ApiResponse<Map<String, Long>>> updateCategory(
		@PathVariable Long categoryId,
		@RequestBody @Validated CategoryRequest.Update request
	) {
		return ResponseEntity.ok(
			ApiResponse.successId(adminCategoryService.updateCategory(categoryId, request.toServiceRequest())));
	}

	@DeleteMapping("/{categoryId}")
	public ResponseEntity<ApiResponse<Map<String, Long>>> deleteCategory(@PathVariable Long categoryId) {
		return ResponseEntity.ok(ApiResponse.successId(adminCategoryService.deleteCategory(categoryId)));
	}
}
