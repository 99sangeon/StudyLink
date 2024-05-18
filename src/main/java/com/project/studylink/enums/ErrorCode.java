package com.project.studylink.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    // 400
    FIELD_ERROR(HttpStatus.BAD_REQUEST, "입력하신 내용을 다시 확인해주세요."),
    HTTP_MESSAGE_NOT_READABLE(HttpStatus.BAD_REQUEST, "요청하신 값을 읽을 수 없습니다."),

    // 401
    EMAIL_AUTH_FAIL(HttpStatus.UNAUTHORIZED, "이메일 인증에 실패했습니다. 입력하신 내용을 다시 확인해주세요."),
    LOGIN_FAIL(HttpStatus.UNAUTHORIZED, "아이디(로그인 전용 이메일) 또는 비밀번호를 잘못 입력했습니다. 입력하신 내용을 다시 확인해주세요."),
    NOT_LOGIN(HttpStatus.UNAUTHORIZED, "로그인 후 이용해주세요."),
    REFRESH_TOKEN_VALIDATE_FAIL(HttpStatus.UNAUTHORIZED, "리프레시 토큰이 유효하지 않습니다. 다시 로그인 해주세요."),
    REFRESH_TOKEN_NOT_IN_REDIS(HttpStatus.UNAUTHORIZED, "리프레시 토큰이 유효하지 않습니다. 다시 로그인 해주세요."),
    OAUTH2_LOGIN_FAIL(HttpStatus.UNAUTHORIZED, "SNS 로그인에 실패했습니다."),

    // 403
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "해당 컨텐츠를 이용할 권한이 없습니다."),

    // 404
    NOT_FOUND_RESOURCE(HttpStatus.NOT_FOUND, "요청하신 url을 찾을 수 없습니다."),

    // 409
    EMAIL_DUPLICATE(HttpStatus.CONFLICT, "이미 가입된 아이디(로그인 전용 이메일)입니다."),

    // 500
    EMAIL_SEND_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "이메일 전송에 실패했습니다. 다시 한번 시도해주세요."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "예상치 못한 오류가 발생했습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
