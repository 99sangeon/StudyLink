package com.project.studylink.service;

import com.project.studylink.dto.request.CategoryRequest;
import com.project.studylink.dto.response.CategoryResponse;
import com.project.studylink.dto.update.CategoryUpdate;
import com.project.studylink.entity.Category;
import com.project.studylink.exception.BusinessException;
import com.project.studylink.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.project.studylink.enums.ErrorCode.CATEGORY_NAME_DUPLICATE;
import static com.project.studylink.enums.ErrorCode.NOT_FOUND_CATEGORY;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryServiceImpl implements CategoryService{

    private final CategoryRepository categoryRepository;

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> getAll() {
        List<Category> categories = categoryRepository.findAll();
        List<CategoryResponse> categoryResponses = new ArrayList<>();

        for(Category category : categories) {
            categoryResponses.add(CategoryResponse.of(category));
        }

        return categoryResponses;
    }

    @Override
    public void register(CategoryRequest categoryRequest) {
        Optional<Category> findCategory = categoryRepository.findByName(categoryRequest.getName());

        if(findCategory.isPresent()) {
            throw new BusinessException(CATEGORY_NAME_DUPLICATE);
        }

        Category category = categoryRequest.toEntity();
        categoryRepository.save(category);
    }

    @Override
    public void update(Long id, CategoryUpdate categoryUpdate) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new BusinessException(NOT_FOUND_CATEGORY));

        Optional<Category> findCategory = categoryRepository.findByName(categoryUpdate.getName());

        if(findCategory.isPresent() && id != findCategory.get().getId()) {
            throw new BusinessException(CATEGORY_NAME_DUPLICATE);
        }

        category.setLogoEmoji(categoryUpdate.getLogoEmoji());
        category.setName(categoryUpdate.getName());
    }

    @Override
    public void delete(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new BusinessException(NOT_FOUND_CATEGORY));

        categoryRepository.delete(category);
    }
}
