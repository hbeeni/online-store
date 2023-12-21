package com.been.onlinestore.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.been.onlinestore.common.ApiResponse;
import com.been.onlinestore.controller.dto.UserRequest;
import com.been.onlinestore.service.UserService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
public class AuthApiController {

	private final UserService userService;

	private final AuthenticationManager authenticationManager;
	private final PasswordEncoder encoder;

	@PostMapping("/api/sign-up")
	public ResponseEntity<ApiResponse<Map<String, Long>>> signUp(@RequestBody @Validated UserRequest.SignUp request) {
		Long id = userService.signUp(request.toServiceRequest(), encoder.encode(request.password()));
		return ResponseEntity.ok(ApiResponse.successId(id));
	}

	@PostMapping("/api/login")
	public ResponseEntity<ApiResponse<Void>> loginUser(@RequestBody @Validated UserRequest.Login request) {
		Authentication authenticationRequest =
			UsernamePasswordAuthenticationToken.unauthenticated(request.uid(), request.password());
		Authentication authentication = this.authenticationManager.authenticate(authenticationRequest);
		SecurityContextHolder.getContext().setAuthentication(authentication);

		return ResponseEntity.ok(ApiResponse.success());
	}
}
