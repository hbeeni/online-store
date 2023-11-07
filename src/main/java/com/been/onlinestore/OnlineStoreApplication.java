package com.been.onlinestore;

import com.been.onlinestore.config.jwt.JwtProperties;
import com.been.onlinestore.domain.User;
import com.been.onlinestore.domain.constant.RoleType;
import com.been.onlinestore.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableConfigurationProperties(JwtProperties.class)
@SpringBootApplication
public class OnlineStoreApplication {

	public static void main(String[] args) {
		SpringApplication.run(OnlineStoreApplication.class, args);
	}

    @Bean
    public CommandLineRunner loadData(UserRepository userRepository, PasswordEncoder encoder) {
        return args -> {
            userRepository.save(User.of("user", encoder.encode("test12"), "user", "testuser@mail.com", "test user", "01012341234"));
            userRepository.save(User.of("admin", encoder.encode("test12"), "admin", "testadmin@mail.com", "test admin", "01012341234", RoleType.ADMIN));
        };
    }
}
