package com.been.onlinestore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.been.onlinestore.config.jwt.JwtProperties;

@EnableConfigurationProperties(JwtProperties.class)
@SpringBootApplication
public class OnlineStoreApplication {

	public static void main(String[] args) {
		SpringApplication.run(OnlineStoreApplication.class, args);
	}
}
