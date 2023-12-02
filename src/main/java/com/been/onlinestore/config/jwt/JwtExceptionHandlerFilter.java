package com.been.onlinestore.config.jwt;

import static com.been.onlinestore.config.jwt.JwtUtils.*;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SecurityException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JwtExceptionHandlerFilter extends OncePerRequestFilter {

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
			FilterChain filterChain) throws ServletException, IOException {
		String requestURI = request.getRequestURI();

		try {
			filterChain.doFilter(request, response);
		} catch (IllegalArgumentException e) {
			log.info("토큰의 값이 존재하지 않습니다. uri = {}", requestURI);
			setErrorResponse(response, JwtErrorCode.NOT_FOUND_TOKEN);
		} catch (SecurityException | MalformedJwtException e) {
			log.info("유효하지 않은 JWT 토큰입니다. uri = {}", requestURI);
			setErrorResponse(response, JwtErrorCode.INVALID_TOKEN);
		} catch (ExpiredJwtException e) {
			log.info("만료된 JWT 토큰입니다. uri = {}", requestURI);
			setErrorResponse(response, JwtErrorCode.EXPIRED_TOKEN);
		} catch (UnsupportedJwtException e) {
			log.info("지원하지 않는 JWT 토큰입니다. uri = {}", requestURI);
			setErrorResponse(response, JwtErrorCode.UNSUPPORTED_TOKEN);
		}
	}
}
