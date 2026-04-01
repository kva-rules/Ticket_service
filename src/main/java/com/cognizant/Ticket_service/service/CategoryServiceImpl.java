package com.cognizant.Ticket_service.service;

import com.cognizant.Ticket_service.entity.Category;
import com.cognizant.Ticket_service.exception.ResourceNotFoundException;
import com.cognizant.Ticket_service.exception.ValidationException;
import com.cognizant.Ticket_service.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    @Transactional
    public Category createCategory(Category category) {
        validateCategory(category);
        if (categoryRepository.existsById(category.getCategoryId())) {
            throw new ValidationException("Category already exists with id: " + category.getCategoryId());
        }
        if (categoryRepository.findAll().stream()
                .anyMatch(existing -> existing.getCategoryName().equalsIgnoreCase(category.getCategoryName().trim()))) {
            throw new ValidationException("Category name already exists: " + category.getCategoryName());
        }
        category.setCategoryName(category.getCategoryName().trim());
        category.setDescription(StringUtils.hasText(category.getDescription()) ? category.getDescription().trim() : null);
        category.setCreatedAt(LocalDateTime.now());
        return categoryRepository.save(category);
    }

    @Override
    @Transactional
    public Category updateCategory(Long categoryId, Category category) {
        Category existing = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + categoryId));

        if (StringUtils.hasText(category.getCategoryName())) {
            String trimmedName = category.getCategoryName().trim();
            if (!trimmedName.equalsIgnoreCase(existing.getCategoryName()) && categoryRepository.findAll().stream()
                    .anyMatch(other -> other.getCategoryName().equalsIgnoreCase(trimmedName))) {
                throw new ValidationException("Category name already exists: " + trimmedName);
            }
            existing.setCategoryName(trimmedName);
        }

        if (StringUtils.hasText(category.getDescription())) {
            existing.setDescription(category.getDescription().trim());
        }

        return categoryRepository.save(existing);
    }

    @Override
    @Transactional
    public void deleteCategory(Long categoryId) {
        Category existing = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + categoryId));
        categoryRepository.delete(existing);
    }

    @Override
    public Category getCategoryById(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + categoryId));
    }

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    private void validateCategory(Category category) {
        if (category == null) {
            throw new ValidationException("Category must not be null");
        }
        if (!StringUtils.hasText(category.getCategoryName())) {
            throw new ValidationException("Category name must not be empty");
        }
    }
}
