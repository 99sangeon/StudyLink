package com.project.studylink.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.project.studylink.enums.ErrorCode;
import lombok.Getter;

import java.util.Map;

@Getter
public class ErrorResponse {

    private final String code;
    private final String message;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Map<String, String> fields;

    public ErrorResponse(ErrorCode errorCode) {
        this.code = errorCode.name();
        this.message = errorCode.getMessage();
    }

    public ErrorResponse(ErrorCode errorCode, Map<String, String> fields) {
        this.code = errorCode.name();
        this.message = errorCode.getMessage();
        this.fields = fields;
    }
}
