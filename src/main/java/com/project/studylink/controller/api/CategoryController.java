package com.project.studylink.controller.api;

import com.project.studylink.dto.request.CategoryRequest;
import com.project.studylink.dto.response.ApiResponse;
import com.project.studylink.dto.response.CategoryResponse;
import com.project.studylink.dto.update.CategoryUpdate;
import com.project.studylink.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CategoryController {

    private final static String API_V1 = "/api/v1/categories";

    private final CategoryService categoryService;

    @GetMapping(API_V1)
    public ResponseEntity<?> getAll() {
        List<CategoryResponse> categoryResponses = categoryService.getAll();
        return ResponseEntity.ok(ApiResponse.success(categoryResponses));
    }

    @PostMapping("/admin" + API_V1)
    public ResponseEntity<?> register(@RequestBody @Valid CategoryRequest categoryRequest) {
        categoryService.register(categoryRequest);
        return ResponseEntity.ok(ApiResponse.success());
    }

    @PatchMapping("/admin" + API_V1 + "/{id}")
    public ResponseEntity<?> update(@PathVariable("id") Long id, @RequestBody @Valid CategoryUpdate categoryUpdate) {
        categoryService.update(id, categoryUpdate);
        return ResponseEntity.ok(ApiResponse.success());
    }

    @DeleteMapping("/admin" + API_V1 + "/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") Long id) {
        categoryService.delete(id);
        return ResponseEntity.ok(ApiResponse.success());
    }
}
