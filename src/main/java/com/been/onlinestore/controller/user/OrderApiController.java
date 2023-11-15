package com.been.onlinestore.controller.user;

import com.been.onlinestore.controller.dto.ApiResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/orders")
@RestController
public class OrderApiController {

    @GetMapping
    public ResponseEntity<?> getOrders(@RequestParam(required = false) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<?> getOrder(@PathVariable Long orderId) {
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PostMapping
    public ResponseEntity<?> order() {
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PutMapping("/{orderId}/cancel")
    public ResponseEntity<?> cancelOrder(@PathVariable Long orderId) {
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
