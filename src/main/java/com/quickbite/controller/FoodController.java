package com.quickbite.controller;

import com.quickbite.model.Food;
import com.quickbite.model.Restaurant;
import com.quickbite.model.User;
import com.quickbite.request.CreateFoodRequest;
import com.quickbite.service.FoodService;
import com.quickbite.service.RestaurantService;
import com.quickbite.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/food")
public class FoodController {
    @Autowired
    private FoodService foodService;

    @Autowired
    private UserService userService;

    @Autowired
    private RestaurantService restaurantService;

    @GetMapping("/search")
    public ResponseEntity<List<Food>> searchFood(@RequestParam String name,
                                           @RequestHeader("Authorization") String jwt) throws Exception {
        User user = userService.findUserByJwtToken(jwt);
        List<Food>foods=foodService.searchFood(name);

        return new ResponseEntity<>(foods, HttpStatus.OK);
    }
    @GetMapping("/restaurant/{restaurantId}")
    public ResponseEntity<List<Food>> getRestaurantFood(
            @RequestParam (required = false) boolean vegetarian,
            @RequestParam (required = false) boolean seasonal,
            @RequestParam (required = false) boolean nonVeg,
            @RequestParam(required = false) String food_category,
            @PathVariable Long restaurantId,
            @RequestHeader("Authorization") String jwt) throws Exception {
        User user = userService.findUserByJwtToken(jwt);

        List<Food>foods=foodService.getRestaurantsFood(restaurantId,vegetarian,nonVeg,seasonal,food_category);

        return new ResponseEntity<>(foods, HttpStatus.OK);
    }
}
