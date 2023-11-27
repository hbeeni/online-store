package com.been.onlinestore.config.jwt;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.been.onlinestore.config.jwt.JwtUtils.setErrorResponse;

@Slf4j
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        log.info("토큰이 존재하지 않거나 Bearer로 시작하지 않습니다. uri = {}", request.getRequestURI());
        setErrorResponse(response, JwtErrorCode.NOT_FOUND_TOKEN);
    }
}
