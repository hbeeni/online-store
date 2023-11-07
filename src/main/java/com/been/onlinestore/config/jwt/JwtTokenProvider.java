package com.been.onlinestore.config.jwt;

import com.been.onlinestore.controller.dto.security.PrincipalDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.security.Key;
import java.util.Date;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtTokenProvider {

    private static final String UID_KEY = "uid";
    private static final String ROLE_KEY = "role";
    private final UserDetailsService userDetailsService;
    private final JwtProperties properties;
    private Key key;

    @PostConstruct
    public void init() {
        byte[] keyBytes = Decoders.BASE64.decode(properties.secret());
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public String createToken(Authentication authentication) {
        log.info("create token");
        PrincipalDetails principal = (PrincipalDetails) (authentication.getPrincipal());
        String role = principal.roleType().getRoleName();
        String uid = principal.uid();

        long now = (new Date()).getTime();
        Date validity = new Date(now + properties.expirationTime());

        return Jwts.builder()
                .setSubject(authentication.getName())
                .claim(UID_KEY, uid)
                .claim(ROLE_KEY, role)
                .signWith(key, SignatureAlgorithm.HS512)
                .setExpiration(validity)
                .compact();
    }

    public Authentication getAuthentication(String token) {
        Claims claims = Jwts
                .parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return new UsernamePasswordAuthenticationToken(
                userDetailsService.loadUserByUsername(claims.get(UID_KEY).toString()),
                token,
                Set.of(new SimpleGrantedAuthority(claims.get(ROLE_KEY).toString()))
        );
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.info("잘못된 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            log.info("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            log.info("지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {
            log.info("JWT 토큰이 잘못되었습니다.");
        }
        return false;
    }
}
