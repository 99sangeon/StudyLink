package com.project.studylink.controller.api;

import com.project.studylink.dto.response.ApiResponse;
import com.project.studylink.dto.response.RegionResponse;
import com.project.studylink.service.RegionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class RegionController {

    private final static String API_V1 = "/api/v1/regions";

    private final RegionService regionService;

    @GetMapping(API_V1)
    public ResponseEntity<?> searchRegions(@RequestParam("keyword") String keyword) {
        List<RegionResponse> regionResponses = regionService.searchRegions(keyword);
        return ResponseEntity.ok(ApiResponse.success(regionResponses));
    }

    @PostMapping(value = "/admin" + API_V1, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> registerRegion(@RequestPart("regionFile") MultipartFile regionFile) {
        regionService.registerRegion(regionFile);
        return ResponseEntity.ok(ApiResponse.success());
    }
}
