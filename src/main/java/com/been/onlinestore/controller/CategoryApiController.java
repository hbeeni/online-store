package com.been.onlinestore.controller;

import com.been.onlinestore.controller.dto.ApiResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/categories")
@RestController
public class CategoryApiController {

    @GetMapping
    public ResponseEntity<?> getCategories() {
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @GetMapping("/{categoryId}")
    public ResponseEntity<?> getCategory(@PathVariable Long categoryId) {
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @GetMapping("/{categoryId}/products")
    public ResponseEntity<?> getAllProductsInCategory(@PathVariable Long categoryId, @RequestParam(required = false) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
