package com.quickbite.controller;

import com.quickbite.model.Food;
import com.quickbite.model.Restaurant;
import com.quickbite.model.User;
import com.quickbite.request.CreateFoodRequest;
import com.quickbite.response.MessageResponse;
import com.quickbite.service.FoodService;
import com.quickbite.service.RestaurantService;
import com.quickbite.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/food")
public class AdminFoodController {

    private final FoodService foodService;
    private final UserService userService;
    private final RestaurantService restaurantService;

    @Autowired
    public AdminFoodController(FoodService foodService, UserService userService, RestaurantService restaurantService) {
        this.foodService = foodService;
        this.userService = userService;
        this.restaurantService = restaurantService;
    }

//    @PostMapping
//    public ResponseEntity<Food> createFood(@RequestBody CreateFoodRequest req,
//                                           @RequestHeader("Authorization") String jwt) throws Exception {
//        User user = userService.findUserByJwtToken(jwt);
//        Restaurant restaurant= restaurantService.findRestaurantById(req.getRestaurantId());
//        Food food=foodService.createFood(req,req.getCategory(), restaurant);
//
//        return new ResponseEntity<>(food, HttpStatus.CREATED);
//    }
@PostMapping
public ResponseEntity<?> createFood(@RequestBody CreateFoodRequest req,
                                    @RequestHeader("Authorization") String jwt) {
    try {
        User user = userService.findUserByJwtToken(jwt);
        Restaurant restaurant = restaurantService.findRestaurantById(req.getRestaurantId());
        Food food = foodService.createFood(req, req.getCategory(), restaurant);
        return new ResponseEntity<>(food, HttpStatus.CREATED);
    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new MessageResponse("Error creating food: " + e.getMessage()));
    }
}
//    @DeleteMapping("/{id}")
//    public ResponseEntity<MessageResponse> deleteFood(@PathVariable Long id,
//                                                      @RequestHeader("Authorization") String jwt) throws Exception {
//        User user = userService.findUserByJwtToken(jwt);
//
//        foodService.deleteFood(id);
//
//        MessageResponse res=new MessageResponse();
//        res.setMessage("Food deleted successfully");
//
//        return new ResponseEntity<>(res, HttpStatus.OK);
//    }
@DeleteMapping("/{id}")
public ResponseEntity<MessageResponse> deleteFood(@PathVariable Long id,
                                                  @RequestHeader("Authorization") String jwt) {
    try {
        User user = userService.findUserByJwtToken(jwt);
        foodService.deleteFood(id);
        return ResponseEntity.ok(new MessageResponse("Food deleted successfully"));
    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new MessageResponse("Error deleting food: " + e.getMessage()));
    }
}

//    @PutMapping("/{id}")
//    public ResponseEntity<Food> updateFoodAvailabilityStatus(@PathVariable Long id,
//                                                      @RequestHeader("Authorization") String jwt) throws Exception {
//        User user = userService.findUserByJwtToken(jwt);
//
//       Food food= foodService.updateAvailabilityStatus(id);
//
//        return new ResponseEntity<>(food, HttpStatus.OK);
//    }
@PutMapping("/{id}")
public ResponseEntity<?> updateFoodAvailabilityStatus(@PathVariable Long id,
                                                      @RequestHeader("Authorization") String jwt) {
    try {
        User user = userService.findUserByJwtToken(jwt);
        Food food = foodService.updateAvailabilityStatus(id);
        return new ResponseEntity<>(food, HttpStatus.OK);
    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new MessageResponse("Error updating food availability status: " + e.getMessage()));
    }
}
}
