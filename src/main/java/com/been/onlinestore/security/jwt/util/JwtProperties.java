package com.been.onlinestore.security.jwt.util;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("my.jwt")
public record JwtProperties(
	String secret,
	int expirationTime,
	String tokenPrefix,
	String headerString
) {
}
