package com.project.studylink.controller.api;

import com.project.studylink.dto.response.RegionResponse;
import com.project.studylink.exception.BusinessException;
import com.project.studylink.service.RegionService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.ArrayList;
import java.util.List;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.project.studylink.enums.ErrorCode.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.times;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = RegionController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
@AutoConfigureRestDocs
class RegionControllerTest {

    private final static String API_V1 = "/api/v1/regions";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RegionService regionService;

    @Test
    @DisplayName("지역 검색 성공")
    void searchRegions() throws Exception {
        // given
        String keyword = "서울";

        List<RegionResponse> regionResponses = new ArrayList<>();
        regionResponses.add(RegionResponse.builder()
                        .id(1L)
                        .emd("전농동")
                        .pullName("서울특별시 동대문구 전농동")
                        .build());
        regionResponses.add(RegionResponse.builder()
                .id(2L)
                .emd("한남동")
                .pullName("서울특별시 용산구 한남동")
                .build());

        given(regionService.searchRegions(keyword)).willReturn(regionResponses);

        // when
        ResultActions resultActions = mockMvc.perform(
                get(API_V1)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .queryParam("keyword", keyword));

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].id").value(regionResponses.get(0).getId()))
                .andExpect(jsonPath("$.data[0].emd").value(regionResponses.get(0).getEmd()))
                .andExpect(jsonPath("$.data[0].pullName").value(regionResponses.get(0).getPullName()))
                .andExpect(jsonPath("$.data[1].id").value(regionResponses.get(1).getId()))
                .andExpect(jsonPath("$.data[1].emd").value(regionResponses.get(1).getEmd()))
                .andExpect(jsonPath("$.data[1].pullName").value(regionResponses.get(1).getPullName()))
                .andDo(document("지역 검색 성공", "지역 검색",
                        queryParameters(
                                parameterWithName("keyword")
                                        .description("검색 키워드")
                        )));
    }

    @Test
    @DisplayName("지역 등록 성공")
    void registerRegion_success() throws Exception {
        // given
        MockMultipartFile regionFile =
                new MockMultipartFile("regionFile", "행정동.xlsx", MediaType.MULTIPART_FORM_DATA_VALUE, "test file".getBytes());

        // when
        ResultActions resultActions = mockMvc.perform(
                multipart("/admin" + API_V1)
                        .file(regionFile)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.MULTIPART_FORM_DATA));

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andDo(document("지역 등록 성공", "지역 등록"));

        then(regionService).should(times(1)).registerRegion(regionFile);
    }

    @Test
    @DisplayName("지역 등록 실패 - 엑셀 파일 읽기 실패")
    void registerRegion_fail_EXCEL_NOT_READABLE() throws Exception {
        // given
        MockMultipartFile regionFile =
                new MockMultipartFile("regionFile", "행정동.xlsx", MediaType.MULTIPART_FORM_DATA_VALUE, "test file".getBytes());

        willThrow(new BusinessException(EXCEL_NOT_READABLE)).given(regionService).registerRegion(regionFile);

        // when
        ResultActions resultActions = mockMvc.perform(
                multipart("/admin" + API_V1)
                        .file(regionFile)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.MULTIPART_FORM_DATA));

        // then
        resultActions
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value(EXCEL_NOT_READABLE.name()))
                .andExpect(jsonPath("$.error.message").value(EXCEL_NOT_READABLE.getMessage()))
                .andDo(document("지역 등록 실패 - 엑셀 파일 읽기 실패"));

    }

    @Test
    @DisplayName("지역 등록 실패 - 필수 최상단 셀(\'시도명\', \'도군구명\', \'읍면동명\') 누락")
    void registerRegion_fail_REGION_CELL_NOT_READABLE() throws Exception {
        // given
        MockMultipartFile regionFile =
                new MockMultipartFile("regionFile", "행정동.xlsx", MediaType.MULTIPART_FORM_DATA_VALUE, "test file".getBytes());

        willThrow(new BusinessException(REGION_CELL_NOT_READABLE)).given(regionService).registerRegion(regionFile);

        // when
        ResultActions resultActions = mockMvc.perform(
                multipart("/admin" + API_V1)
                        .file(regionFile)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.MULTIPART_FORM_DATA));

        // then
        resultActions
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value(REGION_CELL_NOT_READABLE.name()))
                .andExpect(jsonPath("$.error.message").value(REGION_CELL_NOT_READABLE.getMessage()))
                .andDo(document("지역 등록 실패 - 엑셀 파일 읽기 실패"));

    }
}