package com.quickbite.service;

import com.quickbite.dto.RestaurantDto;
import com.quickbite.model.Restaurant;
import com.quickbite.model.User;
import com.quickbite.request.CreateRestaurantRequest;

import java.util.List;

public interface RestaurantService {

    public Restaurant createRestaurant(CreateRestaurantRequest req, User user);

    public Restaurant updateRestaurant(Long restaurantId, CreateRestaurantRequest updatedRestaurant) throws Exception;

    public void deleteRestaurant(Long restaurantId) throws Exception;

    public List<Restaurant> getAllRestaurant();

    public List<Restaurant> searchRestaurant(String keyword);

    public Restaurant findRestaurantById(Long id) throws Exception;

    public Restaurant getRestaurantByUserId(Long userId) throws Exception;

    public RestaurantDto addToFavorites(Long restURndId, User user) throws Exception;

    public Restaurant updateRestaurantStatus(Long Id)throws Exception;
}
