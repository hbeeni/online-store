package com.been.onlinestore.controller;

import com.been.onlinestore.controller.dto.ApiResponse;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api")
@RestController
public class CommonController {

    @GetMapping("/categories")
    public ResponseEntity<?> getCategories() {
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @GetMapping("/categories/{categoryId}/products")
    public ResponseEntity<?> getAllProductsInCategory(@PathVariable Long categoryId, @RequestParam(required = false) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @GetMapping("/products")
    public ResponseEntity<?> getNewArrivals() {
        Pageable pageable = PageRequest.of(0, 9, Sort.by(Sort.Order.desc("createdAt")));
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @GetMapping("/products/{productId}")
    public ResponseEntity<?> getProduct(@PathVariable Long productId) {
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
