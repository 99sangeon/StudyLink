package com.project.studylink.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginRequest {

    private String email;

    private String password;

    public UsernamePasswordAuthenticationToken toAuthenticationToken() {
        return UsernamePasswordAuthenticationToken
                .unauthenticated(email, password);
    }
}
