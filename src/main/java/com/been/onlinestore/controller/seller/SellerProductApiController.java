package com.been.onlinestore.controller.seller;

import com.been.onlinestore.controller.dto.ApiResponse;
import com.been.onlinestore.controller.dto.ProductRequest;
import com.been.onlinestore.controller.dto.security.PrincipalDetails;
import com.been.onlinestore.repository.querydsl.product.AdminProductResponse;
import com.been.onlinestore.repository.querydsl.product.ProductSearchCondition;
import com.been.onlinestore.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RequestMapping("/api/seller/products")
@RestController
public class SellerProductApiController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<AdminProductResponse>>> getProducts(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @ModelAttribute @Validated ProductSearchCondition cond,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(ApiResponse.pagination(productService.findProductsForSeller(principalDetails.id(), cond, pageable)));
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ApiResponse<AdminProductResponse>> getProduct(@AuthenticationPrincipal PrincipalDetails principalDetails, @PathVariable Long productId) {
        return ResponseEntity.ok(ApiResponse.success(productService.findProductForSeller(productId, principalDetails.id())));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Map<String, Long>>> addProduct(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @RequestBody @Validated ProductRequest.Create request
    ) {
        return ResponseEntity.ok(ApiResponse.successId(productService.addProduct(principalDetails.id(), request.toServiceRequest())));
    }

    @PutMapping("/{productId}")
    public ResponseEntity<ApiResponse<Map<String, Long>>> updateProduct(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @PathVariable Long productId,
            @RequestBody @Validated ProductRequest.Update request
    ) {
        return ResponseEntity.ok(ApiResponse.successId(productService.updateProductInfo(productId, principalDetails.id(), request.toServiceRequest())));
    }
}
