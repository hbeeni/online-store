package com.been.onlinestore.config.jwt;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

	private final JwtProperties properties;
	private final JwtTokenProvider jwtTokenProvider;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
			FilterChain filterChain) throws ServletException, IOException {
		String jwt = resolveToken(request);
		String requestURI = request.getRequestURI();

		if (StringUtils.hasText(jwt) && jwtTokenProvider.validateToken(jwt)) {
			Authentication authentication = jwtTokenProvider.getAuthentication(jwt);
			SecurityContextHolder.getContext().setAuthentication(authentication);
			log.debug("Security Context에 '{}' 인증 정보를 저장했습니다. uri: {}", authentication, requestURI);
		} else {
			log.debug("유효한 JWT 토큰이 없습니다. uri: {}", requestURI);
		}

		filterChain.doFilter(request, response);
	}

	private String resolveToken(HttpServletRequest request) {
		String bearerToken = request.getHeader(properties.headerString());

		if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(properties.tokenPrefix())) {
			return bearerToken.replace(properties.tokenPrefix(), "");
		}
		return null;
	}
}
