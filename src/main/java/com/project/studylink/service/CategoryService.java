package com.project.studylink.service;

import com.project.studylink.dto.request.CategoryRequest;
import com.project.studylink.dto.response.CategoryResponse;
import com.project.studylink.dto.update.CategoryUpdate;

import java.util.List;

public interface CategoryService {
    List<CategoryResponse> getAll();

    void register(CategoryRequest categoryRequest);

    void update(Long id, CategoryUpdate categoryUpdate);

    void delete(Long id);
}
