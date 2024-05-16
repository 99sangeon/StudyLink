package com.project.studylink.service;

import com.project.studylink.dto.request.LoginRequest;
import com.project.studylink.dto.response.JwtResponse;
import com.project.studylink.exception.BusinessException;
import com.project.studylink.utils.JwtProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;

import java.util.ArrayList;

import static com.project.studylink.enums.ErrorCode.REFRESH_TOKEN_NOT_IN_REDIS;
import static com.project.studylink.enums.ErrorCode.REFRESH_TOKEN_VALIDATE_FAIL;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    private final int tokenExpiration = 7 * 24 * 60; // 1주일 (일 * 시 * 분)
    private static final String REDIS_KEY_PREFIX = "REFRESH_TOKEN : ";

    @Mock
    private RedisService redisService;

    @Mock
    private JwtProvider jwtProvider;

    @Mock
    private AuthenticationManagerBuilder authenticationManagerBuilder;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthServiceImpl authServiceImpl;

    @Test
    @DisplayName("로그인 성공")
    void login_success() {
        // given
        LoginRequest loginRequest = creatLoginRequest();
        Authentication authentication = loginRequest.toAuthenticationToken();

        given(authenticationManagerBuilder.getObject()).willReturn(authenticationManager);
        given(authenticationManager.authenticate(any())).willReturn(authentication);
        given(jwtProvider.generateAccessToken(authentication)).willReturn("accessToken");
        given(jwtProvider.generateRefreshToken(authentication)).willReturn("refreshToken");

        // when
        JwtResponse jwtResponse = authServiceImpl.login(loginRequest);

        // then
        assertNotNull(jwtResponse);
        assertEquals("accessToken", jwtResponse.getAccessToken());
        assertEquals("refreshToken", jwtResponse.getRefreshToken());

        then(redisService).should(times(1)).save(eq(REDIS_KEY_PREFIX + loginRequest.getEmail()), eq("refreshToken"), eq(tokenExpiration));
    }

    @Test
    @DisplayName("로그인 실패")
    void login_fail() {
        // given
        LoginRequest loginRequest = creatLoginRequest();

        given(authenticationManagerBuilder.getObject()).willReturn(authenticationManager);
        given(authenticationManager.authenticate(any())).willThrow(new BadCredentialsException("로그인 실패"));

        // when
        assertThrows(BadCredentialsException.class, () -> authServiceImpl.login(loginRequest));

        // then
        then(jwtProvider).should(never()).generateAccessToken(any());
        then(jwtProvider).should(never()).generateRefreshToken(any());
        then(redisService).should(never()).save(any(), any(), anyInt());
    }

    @Test
    @DisplayName("JWT 재발급 성공")
    void reIssueAccessToken_success() {
        // given
        String refreshToken = "refreshToken";
        Authentication authentication = createAuthentication();

        given(jwtProvider.validateRefreshToken(refreshToken)).willReturn(true);
        given(jwtProvider.getAuthenticationFromRefreshToken(refreshToken)).willReturn(authentication);
        given(redisService.validateValue(eq(REDIS_KEY_PREFIX + authentication.getName()), eq(refreshToken))).willReturn(true);
        given(jwtProvider.generateAccessToken(authentication)).willReturn("newAccessToken");
        given(jwtProvider.generateRefreshToken(authentication)).willReturn("newRefreshToken");

        // when
        JwtResponse jwtResponse = authServiceImpl.reIssueAccessToken(refreshToken);

        // then
        assertNotNull(jwtResponse);
        assertEquals("newAccessToken", jwtResponse.getAccessToken());
        assertEquals("newRefreshToken", jwtResponse.getRefreshToken());

        then(redisService).should(times(1)).save(eq(REDIS_KEY_PREFIX + authentication.getName()), eq("newRefreshToken"), eq(tokenExpiration));
    }

    @Test
    @DisplayName("JWT 재발급 실패 - 리프레시 토큰 자체 유효성 검증 실패")
    void reIssueAccessToken_fail_invalidToken() {
        // given
        String refreshToken = "refreshToken";

        given(jwtProvider.validateRefreshToken(refreshToken)).willReturn(false);

        // when
        BusinessException exception =
                assertThrows(BusinessException.class, () -> authServiceImpl.reIssueAccessToken(refreshToken));

        // then
        assertEquals(REFRESH_TOKEN_VALIDATE_FAIL, exception.getErrorCode());

        then(jwtProvider).should(never()).getAuthenticationFromRefreshToken(any());
        then(jwtProvider).should(never()).generateAccessToken(any());
        then(jwtProvider).should(never()).generateRefreshToken(any());
    }

    @Test
    @DisplayName("JWT 재발급 실패 - 리프레시 토큰이 레디스에 없음")
    void reIssueAccessToken_fail_tokenNotInRedis() {
        // given
        String refreshToken = "refreshToken";
        Authentication authentication = createAuthentication();

        given(jwtProvider.validateRefreshToken(refreshToken)).willReturn(true);
        given(jwtProvider.getAuthenticationFromRefreshToken(refreshToken)).willReturn(authentication);
        given(redisService.validateValue(eq(REDIS_KEY_PREFIX + authentication.getName()), eq(refreshToken))).willReturn(false);

        // when
        BusinessException exception =
                assertThrows(BusinessException.class, () -> authServiceImpl.reIssueAccessToken(refreshToken));

        // then
        assertEquals(REFRESH_TOKEN_NOT_IN_REDIS, exception.getErrorCode());

        then(jwtProvider).should(never()).generateAccessToken(any());
        then(jwtProvider).should(never()).generateRefreshToken(any());
    }

    private static LoginRequest creatLoginRequest() {
        return LoginRequest.builder()
                .email("test@example.com")
                .password("testPassword")
                .build();
    }

    private static UsernamePasswordAuthenticationToken createAuthentication() {
        return UsernamePasswordAuthenticationToken.authenticated("test@exaple.com", "token", new ArrayList<>());
    }
}