package com.project.studylink.utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtProvider {

    private final String roles = "roles";
    private final String delimiter = ", ";
    private final long accessExpiration;
    private final long refreshExpiration;
    private final Key accesskey;
    private final Key refreshKey;

    public JwtProvider(
            @Value("${spring.jwt.secret.access-token}") String accessSecret,
            @Value("${spring.jwt.secret.refresh-token}") String refreshSecret,
            @Value("${spring.jwt.expiration.access-token}") long accessExpiration,
            @Value("${spring.jwt.expiration.refresh-token}") long refreshExpiration) {

        this.accessExpiration = accessExpiration;
        this.refreshExpiration = refreshExpiration;

        byte[] accessKeyBytes = Decoders.BASE64.decode(accessSecret);
        byte[] refreshKeyBytes = Decoders.BASE64.decode(refreshSecret);
        this.accesskey = Keys.hmacShaKeyFor(accessKeyBytes);
        this.refreshKey = Keys.hmacShaKeyFor(refreshKeyBytes);
    }

    public String generateAccessToken(Authentication authentication) {
        return generateToken(authentication, accesskey, accessExpiration);
    }

    public String generateRefreshToken(Authentication authentication) {
        return generateToken(authentication, refreshKey, refreshExpiration);
    }

    private String generateToken(Authentication authentication, Key key, long expiration) {
        Date now = new Date();

        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(delimiter));

        return Jwts.builder()
                .setSubject(authentication.getName())
                .claim(roles, authorities)
                .setExpiration(new Date(now.getTime() + expiration))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateAccessToken(String token) {
        return validateToken(token, accesskey);
    }

    public boolean validateRefreshToken(String token) {
        return validateToken(token, refreshKey);
    }

    private boolean validateToken(String token, Key key) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.warn("validateToken : {}", "잘못된 JWT 서명입니다.");

        } catch (ExpiredJwtException e) {
            log.warn("validateToken : {}", "만료된 JWT 토큰입니다.");

        } catch (UnsupportedJwtException e) {
            log.warn("validateToken : {}", "지원되지 않는 JWT 토큰입니다.");

        } catch (io.jsonwebtoken.security.SignatureException e) {
            log.warn("validateToken : {}", "JWT 토큰이 잘못되었습니다.");
        }

        return false;
    }

    public Authentication getAuthenticationFromAccessToken(String token) {
        return getAuthentication(token, accesskey);
    }

    public Authentication getAuthenticationFromRefreshToken(String token) {
        return getAuthentication(token, refreshKey);
    }

    private Authentication getAuthentication(String token, Key key) {
        Claims claims = Jwts
                .parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();


        List<SimpleGrantedAuthority> authorityList = Arrays.stream(claims.get(roles).toString().split(delimiter))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        String principal = claims.getSubject();

        return UsernamePasswordAuthenticationToken.authenticated(principal, token, authorityList);
    }
}