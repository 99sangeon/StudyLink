package com.project.studylink.service;

import com.project.studylink.dto.request.LoginRequest;
import com.project.studylink.dto.response.JwtResponse;
import org.springframework.security.core.Authentication;

public interface AuthService {

    JwtResponse login(LoginRequest loginRequest);

    JwtResponse reIssueAccessToken(String refreshToken);

    JwtResponse issueAccessAndRefreshToken(Authentication authentication);
}
