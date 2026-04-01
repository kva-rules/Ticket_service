package com.cognizant.Ticket_service.service;

import com.cognizant.Ticket_service.entity.Category;

import java.util.List;

public interface CategoryService {

    Category createCategory(Category category);

    Category updateCategory(Long categoryId, Category category);

    void deleteCategory(Long categoryId);

    Category getCategoryById(Long categoryId);

    List<Category> getAllCategories();
}
