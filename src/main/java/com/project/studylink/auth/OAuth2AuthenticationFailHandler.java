package com.project.studylink.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.studylink.dto.response.ApiResponse;
import com.project.studylink.dto.response.ErrorResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static com.project.studylink.enums.ErrorCode.OAUTH2_LOGIN_FAIL;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationFailHandler implements AuthenticationFailureHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        log.warn("onAuthenticationFailure : {}", exception.toString());

        response.setContentType("application/json; charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        ApiResponse<Object> apiResponse = ApiResponse.error(new ErrorResponse(OAUTH2_LOGIN_FAIL));
        String result = objectMapper.writeValueAsString(apiResponse);

        response.getWriter().write(result);
    }
}
