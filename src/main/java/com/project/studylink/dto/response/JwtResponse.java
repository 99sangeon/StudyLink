package com.project.studylink.dto.response;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JwtResponse {
    private String accessToken;

    @Setter
    private String refreshToken;
}
