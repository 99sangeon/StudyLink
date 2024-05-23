package com.project.studylink.service;

import com.project.studylink.dto.request.CategoryRequest;
import com.project.studylink.dto.response.CategoryResponse;
import com.project.studylink.dto.update.CategoryUpdate;
import com.project.studylink.entity.Category;
import com.project.studylink.exception.BusinessException;
import com.project.studylink.repository.CategoryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.project.studylink.enums.ErrorCode.CATEGORY_NAME_DUPLICATE;
import static com.project.studylink.enums.ErrorCode.NOT_FOUND_CATEGORY;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    @Test
    @DisplayName("카테고리 조회")
    void getAll() {
        // given
        List<Category> categories = new ArrayList<>();
        categories.add(Category.builder().logoEmoji("❤️").name("Category1").build());
        categories.add(Category.builder().logoEmoji("💕").name("Category2").build());

        given(categoryRepository.findAll()).willReturn(categories);

        // when
        List<CategoryResponse> categoryResponses = categoryService.getAll();

        // then
        assertNotNull(categoryResponses);
        assertEquals(2, categoryResponses.size());
    }

    @Test
    @DisplayName("카테고리 등록 성공")
    void register_Success() {
        // given
        CategoryRequest categoryRequest = createCategoryRequest();
        given(categoryRepository.findByName(categoryRequest.getName())).willReturn(Optional.empty());

        // when
        categoryService.register(categoryRequest);

        // then
        then(categoryRepository).should(times(1)).save(any(Category.class));
    }

    @Test
    @DisplayName("카테고리 등록 실패 - 카테고리명 중복")
    void register_Fail_CATEGORY_NAME_DUPLICATE() {
        // given
        CategoryRequest categoryRequest = createCategoryRequest();
        given(categoryRepository.findByName(categoryRequest.getName())).willReturn(Optional.of(new Category()));

        // when
        BusinessException exception =
                assertThrows(BusinessException.class, () -> categoryService.register(categoryRequest));

        // then
        assertEquals(CATEGORY_NAME_DUPLICATE, exception.getErrorCode());
        then(categoryRepository).should(never()).save(any(Category.class));
    }

    @Test
    @DisplayName("카테고리 업데이트 성공")
    void update_Success() throws IllegalAccessException, NoSuchFieldException {
        // given
        Long id = 1L;
        Category beforeUpdate = Category.builder().logoEmoji("😁").name("beforeUpdate").build();
        CategoryUpdate categoryUpdate = createCategoryUpdate();

        Category findByNameCategory = new Category();
        Field field = findByNameCategory.getClass().getDeclaredField("id");
        field.setAccessible(true);
        field.set(findByNameCategory, id);

        given(categoryRepository.findById(1L)).willReturn(Optional.of(beforeUpdate));
        given(categoryRepository.findByName(categoryUpdate.getName())).willReturn(Optional.of(findByNameCategory));

        // when
        categoryService.update(id, categoryUpdate);

        // then
        assertEquals(categoryUpdate.getLogoEmoji(), beforeUpdate.getLogoEmoji());
        assertEquals(categoryUpdate.getName(), beforeUpdate.getName());
    }

    @Test
    @DisplayName("카테고리 업데이트 실패 - 카테고리 아이디 존재하지 않음")
    void update_Fail_NOT_FOUND_CATEGORY() {
        // given
        Long id = 1L;
        CategoryUpdate categoryUpdate = createCategoryUpdate();

        given(categoryRepository.findById(1L)).willReturn(Optional.empty());

        // when
        BusinessException exception =
                assertThrows(BusinessException.class, () -> categoryService.update(id, categoryUpdate));

        // then
        assertEquals(NOT_FOUND_CATEGORY, exception.getErrorCode());
    }

    @Test
    @DisplayName("카테고리 업데이트 실패 - 카테고리명 중복")
    void update_Fail_CATEGORY_NAME_DUPLICATE() throws NoSuchFieldException, IllegalAccessException {
        // given
        Long id = 1L;
        CategoryUpdate categoryUpdate = createCategoryUpdate();

        Category findByNameCategory = new Category();
        Field field = findByNameCategory.getClass().getDeclaredField("id");
        field.setAccessible(true);
        field.set(findByNameCategory, 2L);

        given(categoryRepository.findById(1L)).willReturn(Optional.of(new Category()));
        given(categoryRepository.findByName(categoryUpdate.getName())).willReturn(Optional.of(findByNameCategory));

        // when
        BusinessException exception =
                assertThrows(BusinessException.class, () -> categoryService.update(id, categoryUpdate));

        // then
        assertEquals(CATEGORY_NAME_DUPLICATE, exception.getErrorCode());
    }

    @Test
    @DisplayName("카테고리 삭제 성공")
    void delete_Success() {
        // given
        Long id = 1L;
        given(categoryRepository.findById(1L)).willReturn(Optional.of(new Category()));

        // when
        categoryService.delete(id);
    }

    @Test
    @DisplayName("카테고리 삭제 실패 - 카테고리 아이디 존재하지 않음")
    void delete_Fail_NOT_FOUND_CATEGORY() {
        // given
        Long id = 1L;
        given(categoryRepository.findById(1L)).willReturn(Optional.empty());

        // when
        BusinessException exception =
                assertThrows(BusinessException.class, () -> categoryService.delete(id));

        // then
        assertEquals(NOT_FOUND_CATEGORY, exception.getErrorCode());
        then(categoryRepository).should(never()).delete(any());
    }


    private CategoryRequest createCategoryRequest() {
        return CategoryRequest.builder()
                .logoEmoji("❤️")
                .name("testName")
                .build();
    }

    private CategoryUpdate createCategoryUpdate() {
        return CategoryUpdate.builder()
                .logoEmoji("❤️")
                .name("testName")
                .build();
    }
}