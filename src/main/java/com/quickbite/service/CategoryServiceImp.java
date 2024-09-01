package com.quickbite.service;

import com.quickbite.model.Category;
import com.quickbite.model.Restaurant;
import com.quickbite.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class CategoryServiceImp implements CategoryService{

    @Autowired
    private RestaurantService restaurantService;

    @Autowired
    private CategoryRepository categoryRepository;

//    @Override
//    public Category createCategory(String name, Long userId) throws Exception {
//        Restaurant restaurant=restaurantService.getRestaurantByUserId(userId);
//        Category category=new Category();
//        category.setName(name);
//        category.setRestaurant(restaurant);
//
//        return categoryRepository.save(category);
//    }
//        @Override
//        public Category createCategory(String name, Long userId) throws Exception {
//            try {
//            Restaurant restaurant = restaurantService.getRestaurantByUserId(userId);
//
//            List<Category> existingCategories = categoryRepository.findByNameAndRestaurantId(name, restaurant.getId());
//            if (!existingCategories.isEmpty()) {
//                // Return the existing category or throw an exception if needed
//                return existingCategories.get(0); // Return the first existing category
//            }
//
//            Category category = new Category();
//            category.setName(name);
//            category.setRestaurant(restaurant);
//
//            return categoryRepository.save(category);
//        } catch (Exception e) {
//        // Log and rethrow the exception
////        log.error("Error creating category", e);
//        throw new RuntimeException("Unable to create category", e);
//    }
//}

    @Override
    public Category createCategory(String name, Long userId, Long restaurantId) throws Exception {
        // Find the restaurant by ID and ensure it belongs to the user
        Restaurant restaurant = restaurantService.findRestaurantById(restaurantId);

        if (!restaurant.getOwner().getId().equals(userId)) {
            throw new Exception("User is not the owner of the specified restaurant.");
        }

        // Check if the category already exists for this restaurant
        List<Category> existingCategories = categoryRepository.findByNameAndRestaurantId(name, restaurant.getId());
        if (!existingCategories.isEmpty()) {
            return existingCategories.get(0); // Return the first existing category
        }
        Category category = new Category();
        category.setName(name);
        category.setRestaurant(restaurant);

        return categoryRepository.save(category);
    }

    private Restaurant selectRestaurantForCategory(List<Restaurant> restaurants) {
        // Implement logic to select one restaurant from the list
        // Example: return the first one or implement a user selection process
        return restaurants.get(0);
    }
    public void removeDuplicateCategories() {
        List<Category> categories = categoryRepository.findAll();
        Map<String, Long> uniqueCategories = new HashMap<>();

        for (Category category : categories) {
            String key = category.getName() + "_" + category.getRestaurant().getId();
            if (uniqueCategories.containsKey(key)) {
                categoryRepository.deleteById(category.getId());
            } else {
                uniqueCategories.put(key, category.getId());
            }
        }
    }

    @Override
    public List<Category> findCategoryByRestaurantId(Long id) throws Exception {
        Restaurant restaurant=restaurantService.findRestaurantById(id);
        return categoryRepository.findByRestaurantId(id) ;
    }

    @Override
    public Category findCategoryById(Long id) throws Exception {
        Optional<Category> optionalCategory=categoryRepository.findById(id);

        if(optionalCategory.isEmpty()){
            throw new Exception("category not found");
        }
        return optionalCategory.get();
    }
}
