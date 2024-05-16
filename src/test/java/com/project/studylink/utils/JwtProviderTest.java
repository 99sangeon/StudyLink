package com.project.studylink.utils;

import io.jsonwebtoken.security.SignatureException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.List;


import static com.project.studylink.enums.Role.MEMBER;
import static org.junit.jupiter.api.Assertions.*;

class JwtProviderTest {

    private JwtProvider jwtProvider;

    @BeforeEach
    void setUp() {
        jwtProvider = new JwtProvider(
                "accessSecretAccessSecretAccessSecretAccessSecretAccessSecret",
                "refreshSecretRefreshSecretRefreshSecretRefreshSecretRefreshSecret",
                3600000, // 1 hour
                604800000 // 1 week
        );
    }

    @Test
    @DisplayName("AccessToken 토큰 발급")
    void generateAccessToken() {
        // Given
        Authentication authentication =  createAuthentication();

        // When
        String accessToken = jwtProvider.generateAccessToken(authentication);

        // Then
        assertNotNull(accessToken);
    }

    @Test
    @DisplayName("RefreshToken 토큰 발급")
    void generateRefreshToken() {
        // Given
        Authentication authentication =  createAuthentication();

        // When
        String refreshToken = jwtProvider.generateRefreshToken(authentication);

        // Then
        assertNotNull(refreshToken);
    }

    @Test
    @DisplayName("AccessToken 토큰 검증")
    void validateAccessToken() {
        // Given
        Authentication authentication =  createAuthentication();
        String accessToken = jwtProvider.generateAccessToken(authentication);

        // When
        boolean accessValid = jwtProvider.validateAccessToken(accessToken);
        boolean refreshValid = jwtProvider.validateRefreshToken(accessToken);

        // Then
        assertTrue(accessValid);
        assertFalse(refreshValid);
    }

    @Test
    @DisplayName("RefreshToken 토큰 검증")
    void validateRefreshToken() {
        // Given
        Authentication authentication =  createAuthentication();
        String refreshToken = jwtProvider.generateRefreshToken(authentication);

        // When
        boolean accessValid = jwtProvider.validateAccessToken(refreshToken);
        boolean refreshValid = jwtProvider.validateRefreshToken(refreshToken);

        // Then
        assertFalse(accessValid);
        assertTrue(refreshValid);
    }

    @Test
    @DisplayName("AccessToken 토큰으로 부터 Authentication 추출")
    void getAuthenticationFromAccessToken() {
        // Given
        Authentication authentication =  createAuthentication();
        String accessToken = jwtProvider.generateAccessToken(authentication);
        String refreshToken = jwtProvider.generateRefreshToken(authentication);

        // When
        Authentication findAuthentication = jwtProvider.getAuthenticationFromAccessToken(accessToken);

        // Then
        assertThrows(SignatureException.class, () -> jwtProvider.getAuthenticationFromAccessToken(refreshToken));

        assertNotNull(findAuthentication);
        assertEquals(authentication.getName(), findAuthentication.getName());
        assertTrue(findAuthentication.getAuthorities().contains(new SimpleGrantedAuthority(MEMBER.getValue())));
    }

    @Test
    @DisplayName("RefreshToken 토큰으로 부터 Authentication 추출")
    void getAuthenticationFromRefreshToken() {
        // Given
        Authentication authentication =  createAuthentication();
        String accessToken = jwtProvider.generateAccessToken(authentication);
        String refreshToken = jwtProvider.generateRefreshToken(authentication);

        // When
        Authentication findAuthentication = jwtProvider.getAuthenticationFromRefreshToken(refreshToken);

        // Then
        assertThrows(SignatureException.class, () -> jwtProvider.getAuthenticationFromRefreshToken(accessToken));

        assertNotNull(findAuthentication);
        assertEquals(authentication.getName(), findAuthentication.getName());
        assertTrue(findAuthentication.getAuthorities().contains(new SimpleGrantedAuthority(MEMBER.getValue())));
    }

    private static UsernamePasswordAuthenticationToken createAuthentication() {
        List<SimpleGrantedAuthority> authorities =  new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(MEMBER.getValue()));

        return UsernamePasswordAuthenticationToken.authenticated("test@exaple.com", "token", authorities);
    }
}