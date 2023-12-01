package com.been.onlinestore.controller;

import com.been.onlinestore.config.jwt.JwtProperties;
import com.been.onlinestore.config.jwt.JwtTokenProvider;
import com.been.onlinestore.controller.dto.ApiResponse;
import com.been.onlinestore.controller.dto.UserRequest;
import com.been.onlinestore.service.UserService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

import java.util.Map;

@RequiredArgsConstructor
@RestController
public class AuthApiController {

	private final UserService userService;

	private final AuthenticationManager authenticationManager;
	private final JwtTokenProvider jwtTokenProvider;
	private final JwtProperties properties;
	private final PasswordEncoder encoder;

	@PostMapping("/api/sign-up")
	public ResponseEntity<ApiResponse<Map<String, Long>>> signUp(@RequestBody @Validated UserRequest.SignUp request) {
		//TODO: 일반 회원과 어드민 회원의 엔드포인트를 다르게 할 지 고민
		Long id = userService.signUp(request.toServiceRequest(), encoder.encode(request.password()));
		return ResponseEntity.ok(ApiResponse.successId(id));
	}

	@PostMapping("/api/login")
	public ResponseEntity<ApiResponse<Map<String, String>>> loginUser(
			@RequestBody @Validated UserRequest.Login request,
			HttpServletResponse response
	) {
		String jwt = createJwtToken(request.uid(), request.password(), response);
		return ResponseEntity.ok(ApiResponse.success(createJwtTokenMap(jwt)));
	}

	private String createJwtToken(String uid, String password, HttpServletResponse response) {
		Authentication authenticationRequest = UsernamePasswordAuthenticationToken.unauthenticated(uid, password);
		Authentication authenticationResponse = authenticationManager.authenticate(authenticationRequest);
		String jwt = jwtTokenProvider.createToken(authenticationResponse);
		response.addHeader(properties.headerString(), properties.tokenPrefix() + jwt);
		return jwt;
	}

	private Map<String, String> createJwtTokenMap(String jwt) {
		return Map.of("token", jwt);
	}
}
