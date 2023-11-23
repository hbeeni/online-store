package com.been.onlinestore.controller;

import com.been.onlinestore.controller.dto.ApiResponse;
import com.been.onlinestore.service.ProductService;
import com.been.onlinestore.service.response.CategoryProductResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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
public class ProductApiController {

    private final ProductService productService;

    @GetMapping({ "/products"})
    public ResponseEntity<ApiResponse<List<CategoryProductResponse>>> getProducts(
            @RequestParam(required = false) String searchName,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(ApiResponse.pagination(productService.findProductsOnSaleForUser(searchName, pageable)));
    }

    @GetMapping("/products/{productId}")
    public ResponseEntity<ApiResponse<CategoryProductResponse>> getProduct(@PathVariable Long productId) {
        return ResponseEntity.ok(ApiResponse.success(productService.findProductOnSaleForUser(productId)));
    }
}
