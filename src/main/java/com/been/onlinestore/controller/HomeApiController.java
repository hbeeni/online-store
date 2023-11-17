package com.been.onlinestore.controller;

import com.been.onlinestore.controller.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class HomeApiController {

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
