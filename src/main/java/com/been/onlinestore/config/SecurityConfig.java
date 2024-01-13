package com.been.onlinestore.config;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.been.onlinestore.config.security.CustomLogoutSuccessHandler;
import com.been.onlinestore.controller.dto.security.PrincipalDetails;
import com.been.onlinestore.domain.constant.RoleType;
import com.been.onlinestore.enums.ErrorMessages;
import com.been.onlinestore.exceptionhandler.security.CustomAccessDeniedHandler;
import com.been.onlinestore.exceptionhandler.security.CustomAuthenticationEntryPoint;
import com.been.onlinestore.service.admin.AdminUserService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@EnableWebSecurity
@Configuration
public class SecurityConfig {

	private static final String[] WHITE_LIST = {
		"/", "/api/login", "/api/sign-up", "/api/categories/**", "/api/products/**"
	};

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		return http
			.formLogin().disable()
			.httpBasic().disable()
			.authorizeRequests(auth -> auth
				.requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
				.mvcMatchers(WHITE_LIST).permitAll()
				.mvcMatchers("/api/admin/**").hasRole(RoleType.ADMIN.name())
				.mvcMatchers("/api/**").hasRole(RoleType.USER.name())
			)
			.logout(logout -> logout
				.logoutUrl("/api/logout").permitAll()
				.logoutSuccessHandler(new CustomLogoutSuccessHandler())
			)
			.exceptionHandling(configurer -> configurer
				.authenticationEntryPoint(new CustomAuthenticationEntryPoint())
				.accessDeniedHandler(new CustomAccessDeniedHandler())
			)
			.csrf(csrf -> csrf.ignoringAntMatchers("/api/**"))
			.build();
	}

	@Bean
	public AuthenticationManager authenticationManager(
		UserDetailsService userDetailsService, PasswordEncoder passwordEncoder
	) {
		DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
		authenticationProvider.setUserDetailsService(userDetailsService);
		authenticationProvider.setPasswordEncoder(passwordEncoder);

		return new ProviderManager(authenticationProvider);
	}

	@Bean
	public UserDetailsService userDetailsService(AdminUserService adminUserService) {
		return uid -> adminUserService
			.searchUser(uid)
			.map(PrincipalDetails::from)
			.orElseThrow(() -> new UsernameNotFoundException(ErrorMessages.NOT_FOUND_USER.getMessage()));
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
