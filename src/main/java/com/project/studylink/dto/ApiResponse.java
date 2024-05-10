package com.project.studylink.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Builder(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ApiResponse<T> {

    private static final String SUCCESS = "success";
    private static final String ERROR = "error";

    private String status;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T data;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private ErrorResponse error;

    public static <T> ApiResponse<T> success() {
        return ApiResponse.<T>builder()
                .status(SUCCESS)
                .build();
    }

    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .status(SUCCESS)
                .data(data)
                .build();
    }

    public static <T> ApiResponse<T> error(ErrorResponse error) {
        return ApiResponse.<T>builder()
                .status(ERROR)
                .error(error)
                .build();
    }
}
