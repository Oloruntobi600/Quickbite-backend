package com.quickbite.controller;

import com.quickbite.model.Restaurant;
import com.quickbite.model.User;
import com.quickbite.request.CreateRestaurantRequest;
import com.quickbite.response.MessageResponse;
import com.quickbite.service.RestaurantService;
import com.quickbite.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/restaurants")
public class AdminRestaurantController {

    @Autowired
    private RestaurantService restaurantService;

    @Autowired
    private UserService userService;

    @PostMapping()
    public ResponseEntity<?> createRestaurant(
            @RequestBody CreateRestaurantRequest req,
            @RequestHeader("Authorization") String jwt
    ) {
        try {
            User user = userService.findUserByJwtToken(jwt); // Assuming this method exists and works correctly
            Restaurant restaurant = restaurantService.createRestaurant(req, user); // Ensure this method signature matches
            return new ResponseEntity<>(restaurant, HttpStatus.CREATED);
        } catch (Exception e) {
            // Log the exception (optional)
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Error creating restaurant: " + e.getMessage()));
        }
    }


    @PutMapping("/{id}")
    public ResponseEntity<Restaurant> updateRestaurant(
            @RequestBody CreateRestaurantRequest req,
            @RequestHeader("Authorization") String jwt,
            @PathVariable Long id
    ) throws Exception {
        User user = userService.findUserByJwtToken(jwt);

        Restaurant restaurant = restaurantService.updateRestaurant(id, req);
        return new ResponseEntity<>(restaurant, HttpStatus.OK);
    }

//    @DeleteMapping("/{id}")
//    public ResponseEntity<MessageResponse> deleteRestaurant(
//
//            @RequestHeader("Authorization") String jwt,
//            @PathVariable Long id
//    ) throws Exception {
//        User user = userService.findUserByJwtToken(jwt);
//
//        restaurantService.deleteRestaurant(id);
//
//        MessageResponse res = new MessageResponse();
//        res.setMessage("restaurant deleted successfully");
//        return new ResponseEntity<>(res, HttpStatus.OK);
//    }
//@DeleteMapping("/{id}")
//public ResponseEntity<MessageResponse> deleteRestaurant(
//        @RequestHeader("Authorization") String jwt,
//        @PathVariable Long id
//) {
//    try {
//        User user = userService.findUserByJwtToken(jwt);
//        restaurantService.deleteRestaurant(id);
//        return ResponseEntity.ok(new MessageResponse("Restaurant deleted successfully"));
//    } catch (Exception e) {
//        e.printStackTrace();
//        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                .body(new MessageResponse("Error deleting restaurant: " + e.getMessage()));
//    }
//}
@DeleteMapping("/{id}")
public ResponseEntity<MessageResponse> deleteRestaurant(
        @RequestHeader("Authorization") String jwt,
        @PathVariable Long id
) {
    if (id == null) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new MessageResponse("Restaurant ID is required"));
    }

    try {
        User user = userService.findUserByJwtToken(jwt);
        restaurantService.deleteRestaurant(id);
        return ResponseEntity.ok(new MessageResponse("Restaurant deleted successfully"));
    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new MessageResponse("Error deleting restaurant: " + e.getMessage()));
    }
}


    @PutMapping("/{id}/status")
    public ResponseEntity<Restaurant> updateRestaurantStatus(

            @RequestHeader("Authorization") String jwt,
            @PathVariable Long id
    ) throws Exception {
        User user = userService.findUserByJwtToken(jwt);

       Restaurant restaurant= restaurantService.updateRestaurantStatus(id);

        return new ResponseEntity<>(restaurant, HttpStatus.OK);
    }

    @GetMapping("/user")
    public ResponseEntity<List<Restaurant>> findRestaurantByUserId(

            @RequestHeader("Authorization") String jwt

    ) throws Exception {
        User user = userService.findUserByJwtToken(jwt);

//        Restaurant restaurant= restaurantService.getRestaurantByUserId(user.getId());
//
//        return new ResponseEntity<>(restaurant, HttpStatus.OK);
//    }
        List<Restaurant> restaurants = restaurantService.getRestaurantsByUserId(user.getId());
        return new ResponseEntity<>(restaurants, HttpStatus.OK);
    }
}
