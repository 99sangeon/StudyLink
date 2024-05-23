package com.project.studylink.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.studylink.dto.request.CategoryRequest;
import com.project.studylink.dto.response.CategoryResponse;
import com.project.studylink.dto.update.CategoryUpdate;
import com.project.studylink.exception.BusinessException;
import com.project.studylink.service.CategoryService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.ArrayList;
import java.util.List;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.project.studylink.enums.ErrorCode.CATEGORY_NAME_DUPLICATE;
import static com.project.studylink.enums.ErrorCode.NOT_FOUND_CATEGORY;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.times;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = CategoryController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
@AutoConfigureRestDocs
class CategoryControllerTest {

    private final static String API_V1 = "/api/v1/categories";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CategoryService categoryService;

    @Test
    @DisplayName("카테고리 조회")
    void getAll() throws Exception {
        // given
        List<CategoryResponse> categoryResponses = createCategoryResponses();
        given(categoryService.getAll()).willReturn(categoryResponses);

        // when
        ResultActions resultActions = mockMvc.perform(
                get(API_V1)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON));

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].logoEmoji").value(categoryResponses.get(0).getLogoEmoji()))
                .andExpect(jsonPath("$.data[0].name").value(categoryResponses.get(0).getName()))
                .andExpect(jsonPath("$.data[1].logoEmoji").value(categoryResponses.get(1).getLogoEmoji()))
                .andExpect(jsonPath("$.data[1].name").value(categoryResponses.get(1).getName()))
                .andDo(document("카테고리 조회", "카테고리 조회"));
    }

    @Test
    @DisplayName("카테고리 등록 성공")
    void register_Success() throws Exception {
        // given
        CategoryRequest categoryRequest = createCategoryRequest();
        String content = objectMapper.writeValueAsString(categoryRequest);

        // when
        ResultActions resultActions = mockMvc.perform(
                post("/admin" + API_V1)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content));

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andDo(document("카테고리 등록 성공", "카테고리 등록"));

        then(categoryService).should(times(1)).register(any());
    }

    @Test
    @DisplayName("카테고리 등록 실패 - 카테고리명 중복")
    void register_Fail_CATEGORY_NAME_DUPLICATE() throws Exception {
        // given
        CategoryRequest categoryRequest = createCategoryRequest();
        String content = objectMapper.writeValueAsString(categoryRequest);
        willThrow(new BusinessException(CATEGORY_NAME_DUPLICATE)).given(categoryService).register(any());

        // when
        ResultActions resultActions = mockMvc.perform(
                post("/admin" + API_V1)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content));

        // then
        resultActions
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value(CATEGORY_NAME_DUPLICATE.name()))
                .andExpect(jsonPath("$.error.message").value(CATEGORY_NAME_DUPLICATE.getMessage()))
                .andDo(document("카테고리 등록 실패 - 카테고리명 중복"));
    }

    @Test
    @DisplayName("카테고리 업데이트 성공")
    void update_Success() throws Exception {
        // given
        CategoryUpdate categoryUpdate = createCategoryUpdate();
        String content = objectMapper.writeValueAsString(categoryUpdate);
        Long id = 1L;

        // when
        ResultActions resultActions = mockMvc.perform(
                patch("/admin" + API_V1 + "/{id}", id)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content));

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andDo(document("카테고리 업데이트 성공", "카테고리 업데이트",
                        pathParameters(
                            parameterWithName("id").description("카테고리 아이디")
                        )
                ));

        then(categoryService).should(times(1)).update(eq(id), any());
    }

    @Test
    @DisplayName("카테고리 업데이트 실패 - 카테고리 아이디 존재하지 않음")
    void update_Fail_NOT_FOUND_CATEGORY() throws Exception {
        // given
        CategoryUpdate categoryUpdate = createCategoryUpdate();
        String content = objectMapper.writeValueAsString(categoryUpdate);
        Long id = 1L;

        willThrow(new BusinessException(NOT_FOUND_CATEGORY)).given(categoryService).update(eq(id), any());

        // when
        ResultActions resultActions = mockMvc.perform(
                patch("/admin" + API_V1 + "/{id}", id)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content));

        // then
        resultActions
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value(NOT_FOUND_CATEGORY.name()))
                .andExpect(jsonPath("$.error.message").value(NOT_FOUND_CATEGORY.getMessage()))
                .andDo(document("카테고리 업데이트 실패 - 카테고리 아이디 존재하지 않음",
                        pathParameters(
                                parameterWithName("id").description("카테고리 아이디")
                        )
                ));
    }

    @Test
    @DisplayName("카테고리 업데이트 실패 - 카테고리명 중복")
    void update_Fail_CATEGORY_NAME_DUPLICATE() throws Exception {
        // given
        CategoryUpdate categoryUpdate = createCategoryUpdate();
        String content = objectMapper.writeValueAsString(categoryUpdate);
        Long id = 1L;

        willThrow(new BusinessException(CATEGORY_NAME_DUPLICATE)).given(categoryService).update(eq(id), any());

        // when
        ResultActions resultActions = mockMvc.perform(
                patch("/admin" + API_V1 + "/{id}", id)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content));

        // then
        resultActions
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value(CATEGORY_NAME_DUPLICATE.name()))
                .andExpect(jsonPath("$.error.message").value(CATEGORY_NAME_DUPLICATE.getMessage()))
                .andDo(document("카테고리 업데이트 실패 - 카테고리명 중복",
                        pathParameters(
                                parameterWithName("id").description("카테고리 아이디")
                        )
                ));
    }

    @Test
    @DisplayName("카테고리 삭제 성공")
    void delete_Success() throws Exception {
        // given
        Long id = 1L;

        // when
        ResultActions resultActions = mockMvc.perform(
                delete("/admin" + API_V1 + "/{id}", id)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON));

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andDo(document("카테고리 삭제 성공", "카테고리 삭제",
                        pathParameters(
                                parameterWithName("id").description("카테고리 아이디")
                        )
                ));

        then(categoryService).should(times(1)).delete(eq(id));
    }

    @Test
    @DisplayName("카테고리 삭제 실패 - 카테고리 아이디 존재하지 않음")
    void delete_Fail_NOT_FOUND_CATEGORY() throws Exception {
        // given
        Long id = 1L;
        willThrow(new BusinessException(NOT_FOUND_CATEGORY)).given(categoryService).delete(id);

        // when
        ResultActions resultActions = mockMvc.perform(
                delete("/admin" + API_V1 + "/{id}", id)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON));

        // then
        resultActions
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value(NOT_FOUND_CATEGORY.name()))
                .andExpect(jsonPath("$.error.message").value(NOT_FOUND_CATEGORY.getMessage()))
                .andDo(document("카테고리 삭제 실패 - 카테고리 아이디 존재하지 않음",
                        pathParameters(
                                parameterWithName("id").description("카테고리 아이디")
                        )
                ));
    }

    private List<CategoryResponse> createCategoryResponses() {
        List<CategoryResponse> categoryResponses = new ArrayList<>();
        categoryResponses.add(CategoryResponse.builder().logoEmoji("❤️").name("testName1").build());
        categoryResponses.add(CategoryResponse.builder().logoEmoji("💕").name("testName2").build());
        return categoryResponses;
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