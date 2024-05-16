package com.project.studylink.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.studylink.dto.response.ApiResponse;
import com.project.studylink.dto.response.ErrorResponse;
import com.project.studylink.enums.ErrorCode;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static com.project.studylink.enums.ErrorCode.ACCESS_DENIED;

@Component
@RequiredArgsConstructor
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        response.setContentType("application/json; charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);

        ApiResponse<Object> apiResponse = ApiResponse.error(new ErrorResponse(ACCESS_DENIED));
        String result = objectMapper.writeValueAsString(apiResponse);

        response.getWriter().write(result);
    }
}
