package com.been.onlinestore.controller.user;

import com.been.onlinestore.controller.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/addresses")
@RestController
public class AddressApiController {

    @GetMapping
    public ResponseEntity<?> getAddresses() {
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @GetMapping("/{addressId}")
    public ResponseEntity<?> getAddress(@PathVariable Long addressId) {
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PostMapping
    public ResponseEntity<?> addAddress() {
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PutMapping("/{addressId}")
    public ResponseEntity<?> updateAddress(@PathVariable Long addressId) {
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @DeleteMapping("/{addressId}")
    public ResponseEntity<?> deleteAddress(@PathVariable Long addressId) {
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
