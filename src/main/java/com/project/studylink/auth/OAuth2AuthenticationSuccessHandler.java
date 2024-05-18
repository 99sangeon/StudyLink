package com.project.studylink.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.studylink.dto.response.ApiResponse;
import com.project.studylink.dto.response.JwtResponse;
import com.project.studylink.service.AuthService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final AuthService authService;
    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        JwtResponse jwtResponse = authService.issueAccessAndRefreshToken(authentication);

        response.setContentType("application/json; charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_OK);

        ApiResponse<Object> apiResponse = ApiResponse.success(jwtResponse);
        String result = objectMapper.writeValueAsString(apiResponse);

        response.getWriter().write(result);
    }
}
