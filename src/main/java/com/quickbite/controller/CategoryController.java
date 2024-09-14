package com.quickbite.controller;

import com.quickbite.model.Category;
import com.quickbite.model.User;
import com.quickbite.request.IngredientCategoryRequest;
import com.quickbite.service.CategoryService;
import com.quickbite.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private UserService userService;

    @PostMapping("/admin/category")
    public ResponseEntity<Category> createCategory(@RequestBody IngredientCategoryRequest request,
                                                   @RequestHeader("Authorization") String jwt) throws Exception {
        System.out.println("Received JWT Token: " + jwt);
        System.out.println("Received request: " + request);
        if (request.getRestaurantId() == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
      User user = userService.findUserByJwtToken(jwt);

      Category createCategory=categoryService.createCategory(request.getName(), user.getId(), request.getRestaurantId());

      return new ResponseEntity<>(createCategory, HttpStatus.CREATED);
    }
    @GetMapping("/category/restaurant/{id}")
    public ResponseEntity<List<Category>> getRestaurantCategory(
            @PathVariable Long id,
            @RequestHeader("Authorization") String jwt) throws Exception {
        System.out.println("Received JWT Token: " + jwt);
        System.out.println("Restaurant ID: " + id);
        User user = userService.findUserByJwtToken(jwt);

       List<Category> categories=categoryService.findCategoryByRestaurantId(id);

        return new ResponseEntity<>(categories, HttpStatus.OK);
    }
}
