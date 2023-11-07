package com.been.onlinestore.controller;

import com.been.onlinestore.config.jwt.JwtProperties;
import com.been.onlinestore.config.jwt.JwtTokenProvider;
import com.been.onlinestore.controller.dto.ApiResponse;
import com.been.onlinestore.controller.dto.request.LoginRequest;
import com.been.onlinestore.controller.dto.request.SignUpRequest;
import com.been.onlinestore.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@RequiredArgsConstructor
@RestController
public class AuthController {

    private final UserService userService;

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtProperties properties;
    private final PasswordEncoder encoder;

    @PostMapping("/api/sign-up")
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

    @PostMapping("/api/login")
    public ResponseEntity<ApiResponse<Map<String, String>>> loginUser(@RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        return ResponseEntity.ok(ApiResponse.success(Map.of("token", createJwtToken(loginRequest.uid(), loginRequest.password(), response))));
    }


    private String createJwtToken(String uid, String password, HttpServletResponse response) {
        Authentication authenticationRequest = UsernamePasswordAuthenticationToken.unauthenticated(uid, password);
        Authentication authenticationResponse = authenticationManager.authenticate(authenticationRequest);
        String jwt = jwtTokenProvider.createToken(authenticationResponse);
        response.addHeader(properties.headerString(), properties.tokenPrefix() + jwt);
        return jwt;
    }
}
