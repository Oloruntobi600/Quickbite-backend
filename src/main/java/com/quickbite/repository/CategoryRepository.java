package com.quickbite.repository;

import com.quickbite.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<Category> findByNameAndRestaurantId(String name, Long restaurantId);

    public List<Category> findByRestaurantId(Long restaurantId);

    void deleteByRestaurantId(Long restaurantId);


}
