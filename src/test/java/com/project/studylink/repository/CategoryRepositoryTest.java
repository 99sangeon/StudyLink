package com.project.studylink.repository;

import com.project.studylink.entity.Category;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    @DisplayName("카테고리명으로 카테고리 조회")
    void findByName() {
        // given
        Category category = createCategory();
        categoryRepository.save(category);
        String notExistName = "notExistName";

        // when
        Optional<Category> existCategory = categoryRepository.findByName(category.getName());
        Optional<Category> notExistCategory = categoryRepository.findByName(notExistName);

        // then
        assertTrue(existCategory.isPresent());
        assertTrue(notExistCategory.isEmpty());
    }

    private Category createCategory() {
        return Category.builder()
                .logoEmoji("❤️")
                .name("testName")
                .build();
    }
}