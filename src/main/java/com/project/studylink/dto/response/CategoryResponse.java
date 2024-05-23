package com.project.studylink.dto.response;

import com.project.studylink.entity.Category;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CategoryResponse {
    private Long id;
    private String logoEmoji;
    private String name;

    public static CategoryResponse of(Category category) {
         return CategoryResponse.builder()
                 .id(category.getId())
                 .logoEmoji(category.getLogoEmoji())
                 .name(category.getName())
                 .build();
    }
}
