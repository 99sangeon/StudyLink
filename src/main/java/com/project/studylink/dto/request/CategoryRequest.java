package com.project.studylink.dto.request;

import com.project.studylink.entity.Category;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryRequest {

    @NotBlank(message = "카테고리 로고(이모지)는 필수 입력 값입니다.")
    private String logoEmoji;

    @Size(min = 2, max = 20, message = "카테고리 명은 최대 2~20자 내에서 입력해주세요.")
    @NotBlank(message = "카테고리명은 필수 입력 값입니다.")
    private String name;

    public Category toEntity() {
        return Category.builder()
                .logoEmoji(logoEmoji)
                .name(name)
                .build();
    }
}
