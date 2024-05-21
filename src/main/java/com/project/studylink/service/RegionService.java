package com.project.studylink.service;

import com.project.studylink.dto.response.RegionResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface RegionService {
    void registerRegion(MultipartFile regionFile);

    List<RegionResponse> searchRegions(String keyword);
}
