package com.been.onlinestore.config;

import java.util.Optional;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import com.been.onlinestore.controller.dto.security.PrincipalDetails;

@EnableJpaAuditing
@Configuration
public class JpaConfig {

	@Bean
	public AuditorAware<String> auditorAware() {
		return () -> Optional.ofNullable(SecurityContextHolder.getContext())
				.map(SecurityContext::getAuthentication)
				.filter(Authentication::isAuthenticated)
				.filter(authentication -> !(authentication instanceof AnonymousAuthenticationToken))
				.map(Authentication::getPrincipal)
				.map(PrincipalDetails.class::cast)
				.map(PrincipalDetails::getUsername);
	}
}
