package com.been.onlinestore.controller;

import com.been.onlinestore.config.jwt.JwtProperties;
import com.been.onlinestore.config.jwt.JwtTokenProvider;
import com.been.onlinestore.controller.dto.ApiResponse;
import com.been.onlinestore.controller.dto.request.LoginRequest;
import com.been.onlinestore.controller.dto.request.SignUpRequest;
import com.been.onlinestore.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class HomeApiController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtProperties properties;
    private final PasswordEncoder encoder;

    @PostMapping("/sign-up")
    public ResponseEntity<ApiResponse<Map<String, Long>>> signUp(@RequestBody SignUpRequest signUpRequest) {
        //TODO: 일반 회원과 어드민 회원의 엔드포인트를 다르게 할 지 고민
        Long id = userService.signUp(
                signUpRequest.uid(),
                encoder.encode(signUpRequest.password()),
                signUpRequest.name(),
                signUpRequest.email(),
                signUpRequest.nickname(),
                signUpRequest.phone(),
                signUpRequest.roleType()
        );

        return ResponseEntity.ok(ApiResponse.success(Map.of("id", id)));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Map<String, String>>> loginUser(@RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        return ResponseEntity.ok(ApiResponse.success(Map.of("token", createJwtToken(loginRequest.uid(), loginRequest.password(), response))));
    }

    @GetMapping("/categories")
    public ResponseEntity<?> getCategories() {
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @GetMapping("/categories/{categoryId}/products")
    public ResponseEntity<?> getAllProductsInCategory(@PathVariable Long categoryId, @RequestParam(required = false) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @GetMapping({"/", "/products"})
    public ResponseEntity<?> getNewArrivals() {
        Pageable pageable = PageRequest.of(0, 9, Sort.by(Sort.Order.desc("createdAt")));
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @GetMapping("/products/{productId}")
    public ResponseEntity<?> getProduct(@PathVariable Long productId) {
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    private String createJwtToken(String uid, String password, HttpServletResponse response) {
        Authentication authenticationRequest = UsernamePasswordAuthenticationToken.unauthenticated(uid, password);
        Authentication authenticationResponse = authenticationManager.authenticate(authenticationRequest);
        String jwt = jwtTokenProvider.createToken(authenticationResponse);
        response.addHeader(properties.headerString(), properties.tokenPrefix() + jwt);
        return jwt;
    }
}
