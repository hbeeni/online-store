package com.been.onlinestore.config;

import java.util.List;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.been.onlinestore.config.jwt.CustomAccessDeniedHandler;
import com.been.onlinestore.config.jwt.CustomAuthenticationEntryPoint;
import com.been.onlinestore.config.jwt.JwtProperties;
import com.been.onlinestore.config.jwt.JwtSecurityConfig;
import com.been.onlinestore.config.jwt.JwtTokenProvider;
import com.been.onlinestore.domain.constant.RoleType;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@EnableWebSecurity
@Configuration
public class SecurityConfig {

	private static final String[] WHITE_LIST = {"/", "/api/login", "/api/sign-up", "/api/categories/**",
			"/api/products/**"};
	private final JwtProperties properties;
	private final JwtTokenProvider jwtTokenProvider;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		return http
				.csrf().disable()
				.cors(httpSecurityCorsConfigurer -> httpSecurityCorsConfigurer.configurationSource(
						corsConfigurationSource()))
				.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
				.and()
				.formLogin().disable()
				.httpBasic().disable()
				.apply(new JwtSecurityConfig(properties, jwtTokenProvider))
				.and()
				.authorizeRequests(auth -> auth
						.requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
						.mvcMatchers(WHITE_LIST).permitAll()
						.mvcMatchers("/api/admin/**").hasRole(RoleType.ADMIN.name())
						.mvcMatchers("/api/seller/**").hasRole(RoleType.SELLER.name())
						.mvcMatchers("/api/**").hasRole(RoleType.USER.name())
				)
				.exceptionHandling(configurer -> configurer
						.authenticationEntryPoint(new CustomAuthenticationEntryPoint())
						.accessDeniedHandler(new CustomAccessDeniedHandler()))
				.logout(logout -> logout.logoutSuccessUrl("/"))
				.build();
	}

	public CorsConfigurationSource corsConfigurationSource() {
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		CorsConfiguration config = new CorsConfiguration();
		config.addAllowedOrigin("*");
		config.addAllowedMethod("*");
		config.setAllowedMethods(List.of("GET", "POST", "DELETE", "PATCH", "OPTION", "PUT"));
		source.registerCorsConfiguration("/**", config);

		return source;
	}

	@Bean
	public AuthenticationManager authenticationManager(UserDetailsService userDetailsService,
			PasswordEncoder passwordEncoder) {
		DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
		authenticationProvider.setUserDetailsService(userDetailsService);
		authenticationProvider.setPasswordEncoder(passwordEncoder);

		return new ProviderManager(authenticationProvider);
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
