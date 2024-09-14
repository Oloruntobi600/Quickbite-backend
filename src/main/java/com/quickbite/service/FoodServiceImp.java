package com.quickbite.service;

import com.quickbite.model.Category;
import com.quickbite.model.Food;
import com.quickbite.model.Restaurant;
import com.quickbite.repository.CartItemRepository;
import com.quickbite.repository.FoodRepository;
import com.quickbite.repository.OrderItemRepository;
import com.quickbite.request.CreateFoodRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FoodServiceImp implements FoodService{

    @Autowired
    private FoodRepository foodRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private CartItemRepository cartItemRepository;
    @Override
    public Food createFood(CreateFoodRequest req, Category category, Restaurant restaurant) {
        Food food = new Food();
        food.setFoodCategory(category);
        food.setRestaurant(restaurant);
        food.setDescription(req.getDescription());
        food.setImages(req.getImages());
        food.setName(req.getName());
        food.setPrice(req.getPrice());
        food.setIngredients(req.getIngredients());
        food.setSeasonal(req.isSeasonal());
        food.setVegetarian(req.isVegetarian());
        food.setCreationDate(new Date());


        Food savedFood= foodRepository.save(food);
        restaurant.getFoods().add(savedFood);

        return savedFood;
    }

    @Override
    public void deleteFood(Long foodId) throws Exception {

//        Food food=findFoodById(foodId);
//        food.setRestaurant(null);
//        foodRepository.save(food);

        if (foodId == null) {
            throw new IllegalArgumentException("Food ID must not be null");
        }

        // Find the food item
        Food food = foodRepository.findById(foodId)
                .orElseThrow(() -> new Exception("Food not found with id " + foodId));

        // Remove associated order items
        orderItemRepository.deleteByFoodId(foodId);

        // Delete the food item
        foodRepository.delete(food);
        cartItemRepository.deleteByFoodId(foodId);
    }

    @Override
    public List<Food> getRestaurantsFood(Long restaurantId,
                                         boolean isVegetarian,
                                         boolean isNonVeg,
                                         boolean isSeasonal, String foodCategory) {

        List<Food> foods= foodRepository.findByRestaurantId(restaurantId);

        if(isVegetarian){
            foods=filterByVegetarian(foods, isVegetarian);
        }
        if(isNonVeg){
            foods=filterByNonVeg(foods, isNonVeg);
        }
        if(isSeasonal){
            foods=filterBySeasonal(foods, isSeasonal);
        }
        if(foodCategory!=null && !foodCategory.equals("")){
            foods=filterByCategory(foods, foodCategory);
        }

        return foods;
    }

    private List<Food> filterByCategory(List<Food> foods, String foodCategory) {
        return foods.stream().filter(food -> {
            if(food.getFoodCategory()!=null){
                return food.getFoodCategory().getName().equals(foodCategory);
            }
            return false;
        }).collect(Collectors.toList());
    }

    private List<Food> filterBySeasonal(List<Food> foods, boolean isSeasonal) {
        return foods.stream().filter(food -> food.isSeasonal()==isSeasonal).collect(Collectors.toList());
    }

    private List<Food> filterByNonVeg(List<Food> foods, boolean isNonVeg) {
        return foods.stream().filter(food -> food.isVegetarian()==false).collect(Collectors.toList());
    }

    private List<Food> filterByVegetarian(List<Food> foods, boolean isVegetarian) {
        return foods.stream().filter(food -> food.isVegetarian()==isVegetarian).collect(Collectors.toList());
    }

    @Override
    public List<Food> searchFood(String keyword) {
        return foodRepository.searchFood(keyword);
    }

    @Override
    public Food findFoodById(Long foodId) throws Exception {
        Optional<Food> optionalFood = foodRepository.findById(foodId);

        if(optionalFood.isEmpty()){
            throw new Exception("food does not exist....");
        }
        return optionalFood.get();
    }

    @Override
    public Food updateAvailabilityStatus(Long foodId) throws Exception {
        Food food = findFoodById(foodId);
        food.setAvailable(!food.isAvailable());
        return foodRepository.save(food);
    }
}
