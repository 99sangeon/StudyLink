package com.project.studylink.dto.request;

import com.project.studylink.entity.Member;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberRequest {

    @Email(message = "이메일 형식이 올바르지 않습니다.")
    @NotBlank(message = "이메일은 필수 입력 값입니다.")
    private String email;

    @Pattern(regexp = "(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\W)(?=\\S+$).{8,16}", message = "비밀번호는 8~16자 영문 대 소문자, 숫자, 특수문자를 사용해주세요.")
    @NotBlank(message = "비밀번호는 필수 입력 값입니다.")
    private String password;

    @Pattern(regexp = "^[ㄱ-ㅎ가-힣a-zA-Z0-9-_]{2,20}$", message = "닉네임은 특수문자를 제외한 2~20자 내에서 입력해주세요.")
    @NotBlank(message = "닉네임은 필수 입력 값입니다.")
    private String nickname;

    @Size(max = 255, message = "자기소개는 255자 이내로 입력해주세요.")
    @NotNull(message = "자기소개는 null 값일 수 없습니다.")
    private String introduction;

    @NotBlank(message = "인증번호는 필수 입력 값입니다.")
    private String authNum;

    public Member toEntity() {
        return Member.builder()
                .username(email)
                .password(password)
                .email(email)
                .nickname(nickname)
                .introduction(introduction)
                .build();
    }
}
