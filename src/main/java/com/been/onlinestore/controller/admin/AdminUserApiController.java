package com.been.onlinestore.controller.admin;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.been.onlinestore.controller.dto.ApiResponse;
import com.been.onlinestore.service.admin.AdminUserService;
import com.been.onlinestore.service.dto.response.UserResponse;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/api/admin/users")
@RestController
public class AdminUserApiController {

	private final AdminUserService adminUserService;

	@GetMapping
	public ResponseEntity<ApiResponse<List<UserResponse>>> getUsers(
		@PageableDefault(sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {
		return ResponseEntity.ok(ApiResponse.pagination(adminUserService.findUsers(pageable)));
	}

	@GetMapping("/{userId}")
	public ResponseEntity<ApiResponse<UserResponse>> getUser(@PathVariable Long userId) {
		return ResponseEntity.ok(ApiResponse.success(adminUserService.findUser(userId)));
	}
}
