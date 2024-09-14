package com.quickbite.controller;

import com.quickbite.model.IngredientCategory;
import com.quickbite.model.IngredientsItem;
import com.quickbite.request.IngredientCategoryRequest;
import com.quickbite.request.IngredientRequest;
import com.quickbite.service.IngredientsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/ingredients")
public class IngredientController {

    @Autowired
    private IngredientsService ingredientsService;

//    @PostMapping("/category")
//    public ResponseEntity<IngredientCategory> createIngredientCategory(
//            @RequestBody IngredientCategoryRequest req
//            ) throws Exception {
//        IngredientCategory item=ingredientsService.createIngredientCategory(req.getName(), req.getRestaurantId());
//        return new ResponseEntity<>(item, HttpStatus.CREATED);
//    }
@PostMapping("/category")
public ResponseEntity<?> createIngredientCategory(
        @RequestBody IngredientCategoryRequest req
) {
    if (req.getRestaurantId() == null) {
        return ResponseEntity.badRequest().body("The given id must not be null");
    }
    try {
        IngredientCategory item = ingredientsService.createIngredientCategory(req.getName(), req.getRestaurantId());
        return new ResponseEntity<>(item, HttpStatus.CREATED);
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating ingredient category");
    }
}


//    @PostMapping()
//    public ResponseEntity<IngredientsItem> createIngredientItem(
//            @RequestBody IngredientRequest req
//    ) throws Exception {
//        IngredientsItem item=ingredientsService.createIngredientItem(req.getRestaurantId(), req.getName(), req.getCategoryId());
//        return new ResponseEntity<>(item, HttpStatus.CREATED);
//    }

    @PostMapping()
    public ResponseEntity<?> createIngredientItem(
            @RequestBody IngredientRequest req
    ) {
        if (req.getRestaurantId() == null || req.getCategoryId() == null || req.getName() == null || req.getName().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("The given id or name must not be null");
        }
        try {
            IngredientsItem item = ingredientsService.createIngredientItem(req.getRestaurantId(), req.getName(), req.getCategoryId());
            return new ResponseEntity<>(item, HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating ingredient item: " + e.getMessage());
        }
    }

//    @PutMapping("/{id}/stock")
//    public ResponseEntity<IngredientsItem> updateIngredientStock(
//            @PathVariable Long id
//    ) throws Exception {
//        IngredientsItem item=ingredientsService.updateStock(id);
//        return new ResponseEntity<>(item, HttpStatus.OK);
//    }

    @PutMapping("/{id}/stock")
    public ResponseEntity<?> updateIngredientStock(
            @PathVariable Long id
    ) {
        try {
            IngredientsItem item = ingredientsService.updateStock(id);
            return new ResponseEntity<>(item, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating ingredient stock: " + e.getMessage());
        }
    }


//    @GetMapping("/restaurant/{id}")
//    public ResponseEntity<List<IngredientsItem>> getRestaurantIngredient(
//            @PathVariable Long id
//    ) throws Exception {
//      List<IngredientsItem> items=ingredientsService.findRestaurantsIngredients(id);
//        return new ResponseEntity<>(items, HttpStatus.OK);
//    }

    @GetMapping("/restaurant/{id}")
    public ResponseEntity<?> getRestaurantIngredient(
            @PathVariable Long id
    ) {
        try {
            List<IngredientsItem> items = ingredientsService.findRestaurantsIngredients(id);
            return new ResponseEntity<>(items, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error retrieving ingredients: " + e.getMessage());
        }
    }

//    @GetMapping("/restaurant/{id}/category")
//    public ResponseEntity<List<IngredientCategory>> getRestaurantIngredientCategory(
//            @PathVariable Long id
//    ) throws Exception {
//        List<IngredientCategory> items=ingredientsService.findIngredientCategoryByRestaurantId(id);
//        return new ResponseEntity<>(items, HttpStatus.OK);
//    }
//}

    @GetMapping("/restaurant/{id}/category")
    public ResponseEntity<?> getRestaurantIngredientCategory(
            @PathVariable Long id
    ) {
        try {
            List<IngredientCategory> items = ingredientsService.findIngredientCategoryByRestaurantId(id);
            return new ResponseEntity<>(items, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error retrieving ingredient categories: " + e.getMessage());
        }
    }
}
