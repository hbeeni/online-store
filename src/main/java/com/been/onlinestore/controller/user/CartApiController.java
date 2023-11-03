package com.been.onlinestore.controller.user;

import com.been.onlinestore.controller.dto.ApiResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/user/carts")
@RestController
public class CartApiController {

    @GetMapping
    public ResponseEntity<?> getProductsInCart(@RequestParam(required = false) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PostMapping
    public ResponseEntity<?> addProductToCart() {
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PutMapping("/products/{cartProductId}")
    public ResponseEntity<?> updateProductInCart(@PathVariable Long cartProductId) {
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @DeleteMapping
    public ResponseEntity<?> deleteCart() {
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
