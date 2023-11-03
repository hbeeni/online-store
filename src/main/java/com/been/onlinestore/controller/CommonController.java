package com.been.onlinestore.controller;

import com.been.onlinestore.controller.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/common")
@RestController
public class CommonController {

    @PutMapping("/info")
    public ResponseEntity<?> updateUserInfo() {
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
