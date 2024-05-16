package com.project.studylink.service;

import com.project.studylink.dto.request.LoginRequest;
import com.project.studylink.dto.response.JwtResponse;
import com.project.studylink.exception.BusinessException;
import com.project.studylink.utils.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import static com.project.studylink.enums.ErrorCode.REFRESH_TOKEN_NOT_IN_REDIS;
import static com.project.studylink.enums.ErrorCode.REFRESH_TOKEN_VALIDATE_FAIL;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService{

    private final RedisService redisService;
    private final JwtProvider jwtProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final int tokenExpiration = 7 * 24 * 60; // 1주일 (일 * 시 * 분)
    private static final String REDIS_KEY_PREFIX = "REFRESH_TOKEN : ";

    @Override
    public JwtResponse login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManagerBuilder.getObject()
                .authenticate(loginRequest.toAuthenticationToken());

        // 토큰 발급 및 리프레시 토큰 레디스에 저장
        String accessToken  = jwtProvider.generateAccessToken(authentication);
        String refreshToken  = jwtProvider.generateRefreshToken(authentication);
        redisService.save(REDIS_KEY_PREFIX + authentication.getName(), refreshToken, tokenExpiration);

        return new JwtResponse(accessToken, refreshToken);
    }

    @Override
    public JwtResponse reIssueAccessToken(String refreshToken) {
        // 리프레시 토큰 자체 유효성 검증
        if(!StringUtils.hasText(refreshToken) || !jwtProvider.validateRefreshToken(refreshToken)) {
            throw new BusinessException(REFRESH_TOKEN_VALIDATE_FAIL);
        }

        Authentication authentication = jwtProvider.getAuthenticationFromRefreshToken(refreshToken);

        // 리프레시 토큰 사용 가능 여부 검증 -> 리프레시 토큰 자체가 유효하더라도 레디스에는 가장 최근에 발급된 리프레시 토큰만 관리함
        if(!redisService.validateValue(REDIS_KEY_PREFIX + authentication.getName(), refreshToken)) {
            throw new BusinessException(REFRESH_TOKEN_NOT_IN_REDIS);
        }

        // 토큰 발급 및 새로운 리프레시 토큰 레디스에 저장
        String accessToken  = jwtProvider.generateAccessToken(authentication);
        String newRefreshToken  = jwtProvider.generateRefreshToken(authentication);
        redisService.save(REDIS_KEY_PREFIX + authentication.getName(), newRefreshToken, tokenExpiration);

        return new JwtResponse(accessToken, newRefreshToken);
    }
}
