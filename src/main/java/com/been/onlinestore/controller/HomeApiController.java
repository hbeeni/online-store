package com.been.onlinestore.controller;

import com.been.onlinestore.controller.dto.ApiResponse;
import com.been.onlinestore.controller.dto.response.CategoryResponse;
import com.been.onlinestore.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class HomeApiController {

    private final CategoryService categoryService;

    @GetMapping("/categories")
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getCategories() {
        List<CategoryResponse> categoryResponses = categoryService.findCategories().stream()
                .map(CategoryResponse::from)
                .toList();
        return ResponseEntity.ok(ApiResponse.success(categoryResponses));
    }

    @GetMapping("/categories/{categoryId}/products")
    public ResponseEntity<?> getAllProductsInCategory(@PathVariable Long categoryId, @RequestParam(required = false) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @GetMapping({"/", "/products"})
    public ResponseEntity<?> getNewArrivals() {
        Pageable pageable = PageRequest.of(0, 9, Sort.by(Sort.Order.desc("createdAt")));
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @GetMapping("/products/{productId}")
    public ResponseEntity<?> getProduct(@PathVariable Long productId) {
        return ResponseEntity.ok(ApiResponse.success(null));
    }

}
