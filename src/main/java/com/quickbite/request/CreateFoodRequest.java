package com.quickbite.request;

import com.quickbite.model.Category;
import com.quickbite.model.IngredientsItem;
import lombok.Data;
import org.springframework.stereotype.Service;

import java.util.List;

//@Service
@Data
public class CreateFoodRequest {

    private String name;

    private String description;

    private Long price;

    private Category category;

    private List<String> images;

    private Long restaurantId;

    private boolean vegetarian;

    private boolean seasonal;

    private List<IngredientsItem> ingredients;
}
