package com.been.onlinestore.config;

import com.been.onlinestore.domain.constant.RoleType;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeRequests(auth -> auth
                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                        .mvcMatchers("/api/common/**").authenticated()
                        .mvcMatchers("/api/admin/**").hasRole(RoleType.ADMIN.name())
                        .mvcMatchers("/api/seller/**").hasRole(RoleType.SELLER.name())
                        .mvcMatchers("/api/user/**").hasRole(RoleType.USER.name())
                        .anyRequest().permitAll()
                )
                .csrf(csrf -> csrf.ignoringAntMatchers("/api/**"))
                .logout(logout -> logout.logoutSuccessUrl("/"))
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
