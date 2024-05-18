package com.project.studylink.auth;

import com.project.studylink.entity.Member;
import com.project.studylink.enums.Role;
import com.project.studylink.enums.Sns;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.AuthenticationException;

import java.util.Map;
import java.util.UUID;

@Builder
@Getter
public class OAuth2UserInfo {

    private String username;
    private String nickName;
    private String email;
    private String profileImg;
    private Sns sns;

    public static OAuth2UserInfo of(String registrationId, String userNameAttributeName, Map<String, Object> attributes)  {
        return switch (registrationId) {
            case "google" -> ofGoogle(userNameAttributeName, attributes);
            case "kakao" -> ofKakao(userNameAttributeName, attributes);
            default -> throw new AuthenticationException(registrationId + " 로그인은 지원되지 않습니다.") {};
        };
    }

    private static OAuth2UserInfo ofGoogle(String userNameAttributeName, Map<String, Object> attributes) {
        return OAuth2UserInfo.builder()
                .username((String) attributes.get(userNameAttributeName))
                .nickName((String) attributes.get("name"))
                .email((String) attributes.get("email"))
                .profileImg((String) attributes.get("picture"))
                .sns(Sns.GOOGLE)
                .build();
    }

    private static OAuth2UserInfo ofKakao(String userNameAttributeName, Map<String, Object> attributes) {
        Map<String, Object> account = (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> profile = (Map<String, Object>) account.get("profile");

        return OAuth2UserInfo.builder()
                .username(String.valueOf(attributes.get(userNameAttributeName)))
                .nickName((String) profile.get("nickname"))
                .email("")
                .profileImg((String) profile.get("profile_image_url"))
                .sns(Sns.KAKAO)
                .build();
    }

    public Member toEntity() {
        String randomPassword = String.valueOf(UUID.randomUUID());

        return Member.builder()
                .username(username)
                .email(email)
                .profileImg(profileImg)
                .sns(sns)
                .password(randomPassword)
                .nickname(nickName)
                .role(Role.MEMBER)
                .build();
    }
}
