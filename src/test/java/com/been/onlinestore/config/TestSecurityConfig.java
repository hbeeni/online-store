package com.been.onlinestore.config;

import com.been.onlinestore.controller.dto.security.PrincipalDetails;
import com.been.onlinestore.domain.constant.RoleType;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;

@TestConfiguration
public class TestSecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf().disable()
                .authorizeRequests(auth -> auth.anyRequest().permitAll())
                .build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> PrincipalDetails.of(
                1L,
                "seller",
                "pw",
                RoleType.SELLER,
                "seller",
                "seller@mail.com",
                "seller",
                "01011112222"
        );
    }
}
