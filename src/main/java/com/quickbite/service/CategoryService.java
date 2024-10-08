package com.quickbite.service;

import com.quickbite.model.Category;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface CategoryService {

    public Category createCategory(String name, Long userId, Long restaurantId) throws Exception;

    public List<Category> findCategoryByRestaurantId(Long id) throws Exception;

    public Category findCategoryById(Long id) throws Exception;

    @Transactional
    void deleteCategory(Long categoryId) throws Exception;

}
