package com.project.studylink.controller.api;

import com.project.studylink.dto.request.LoginRequest;
import com.project.studylink.dto.response.ApiResponse;
import com.project.studylink.dto.response.JwtResponse;
import com.project.studylink.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final static String API_V1 = "/api/v1/auth";

    private final AuthService authService;

    @PostMapping(API_V1 + "/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginRequest loginRequest) {
        JwtResponse jwtResponse = authService.login(loginRequest);
        return ResponseEntity.ok(ApiResponse.success(jwtResponse));
    }

    @GetMapping(API_V1 + "/reissue/token")
    public ResponseEntity<?> reIssueAccessToken(@CookieValue(value = "refreshToken", required = false) String refreshToken) {
        JwtResponse jwtResponse = authService.reIssueAccessToken(refreshToken);
        return ResponseEntity.ok(ApiResponse.success(jwtResponse));
    }

    @GetMapping("/auth/test")
    public ResponseEntity<?> test() {
        return ResponseEntity.ok(ApiResponse.success());
    }

}
