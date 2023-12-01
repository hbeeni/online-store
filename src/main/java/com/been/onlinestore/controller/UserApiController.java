package com.been.onlinestore.controller;

import com.been.onlinestore.controller.dto.ApiResponse;
import com.been.onlinestore.controller.dto.UserRequest;
import com.been.onlinestore.controller.dto.security.PrincipalDetails;
import com.been.onlinestore.service.UserService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RequiredArgsConstructor
@RequestMapping("/api/users")
@RestController
public class UserApiController {

	private final UserService userService;

	@PutMapping("/info")
	public ResponseEntity<ApiResponse<Map<String, Long>>> updateUserInfo(
			@AuthenticationPrincipal PrincipalDetails principalDetails,
			@RequestBody @Validated UserRequest.Update request
	) {
		return ResponseEntity.ok(ApiResponse.successId(
				userService.updateInfo(principalDetails.id(), request.nickname(), request.phone())));
	}
}
