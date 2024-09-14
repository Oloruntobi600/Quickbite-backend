package com.quickbite.service;

import com.quickbite.dto.RestaurantDto;
import com.quickbite.model.Address;
import com.quickbite.model.Restaurant;
import com.quickbite.model.User;
import com.quickbite.repository.*;
import com.quickbite.request.CreateRestaurantRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class RestaurantServiceImpl implements RestaurantService{

    private final RestaurantRepository restaurantRepository;
    private final AddressRepository addressRepository;
    private final UserRepository userRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    public RestaurantServiceImpl(RestaurantRepository restaurantRepository,
                                 AddressRepository addressRepository,
                                 UserRepository userRepository) {
        this.restaurantRepository = restaurantRepository;
        this.addressRepository = addressRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public Restaurant createRestaurant(CreateRestaurantRequest req, User user) {

        Address address =addressRepository.save(req.getAddress());

        Restaurant restaurant = new Restaurant();
        restaurant.setAddress(address);
        restaurant.setContactInformation(req.getContactInformation());
        restaurant.setCuisineType(req.getCuisineType());
        restaurant.setDescription(req.getDescription());
        restaurant.setImages(req.getImages());
        restaurant.setName(req.getName());
        restaurant.setOpeningHours(req.getOpeningHours());
        restaurant.setRegistrationDate(LocalDateTime.now());
        restaurant.setOwner(user);


        return restaurantRepository.save(restaurant);
    }


    @Override
    public Restaurant updateRestaurant(Long restaurantId, CreateRestaurantRequest updatedRestaurant) throws Exception {
        if (restaurantId == null) {
            throw new IllegalArgumentException("Restaurant ID must not be null");
        }
        Restaurant restaurant = findRestaurantById(restaurantId);

        if (updatedRestaurant.getCuisineType() != null) {
            restaurant.setCuisineType(updatedRestaurant.getCuisineType());
        }
        if (updatedRestaurant.getDescription() != null) {
            restaurant.setDescription(updatedRestaurant.getDescription());
        }
        if (updatedRestaurant.getName() != null) {
            restaurant.setName(updatedRestaurant.getName());
        }

        return restaurantRepository.save(restaurant);
    }



    @Override
    @Transactional
    public void deleteRestaurant(Long restaurantId) throws Exception {
        if (restaurantId == null) {
            throw new IllegalArgumentException("Restaurant ID must not be null");
        }
        // Find the restaurant
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new Exception("Restaurant not found with id " + restaurantId));

        // Optionally: Delete related cart items
        cartItemRepository.deleteByFoodId(restaurantId);
        categoryRepository.deleteByRestaurantId(restaurantId);

        // Delete the restaurant
        restaurantRepository.delete(restaurant);
    }


    @Override
    public List<Restaurant> getAllRestaurant() {

        return restaurantRepository.findAll();
    }

    @Override
    public List<Restaurant> searchRestaurant(String keyword) {
        return restaurantRepository.findBySearchQuery(keyword);
    }


    @Override
    public Restaurant findRestaurantById(Long id) throws Exception {
        if (id == null) {
            throw new IllegalArgumentException("The given id must not be null");
        }
        return restaurantRepository.findById(id)
                .orElseThrow(() -> new Exception("Restaurant not found with id " + id));
    }


    @Override
    public Restaurant getRestaurantByUserId(Long userId) throws Exception {
        return null;
    }


@Override
public List<Restaurant> getRestaurantsByUserId(Long userId) {
    List<Restaurant> restaurants = restaurantRepository.findByOwnerId(userId);
    if (restaurants.isEmpty()) {
        throw new RuntimeException("No restaurants found for user ID: " + userId);
    }
    return restaurants;
}


    @Override
    public RestaurantDto addToFavorites(Long restaurantId, User user) throws Exception {

        Restaurant restaurant = findRestaurantById(restaurantId);

        RestaurantDto dto = new RestaurantDto();
        dto.setDescription(restaurant.getDescription());
        dto.setImages(restaurant.getImages());
        dto.setTitle(restaurant.getName());
        dto.setId(restaurantId);



        Set<RestaurantDto> favorites = new HashSet<>(user.getFavorites());

        if (favorites.contains(dto)) {
            favorites.remove(dto);
        } else {
            favorites.add(dto);
        }

        user.setFavorites(new ArrayList<>(favorites));
        userRepository.save(user);
        return dto;
    }

    @Override
    public Restaurant updateRestaurantStatus(Long id) throws Exception {
        Restaurant restaurant = findRestaurantById(id);
        restaurant.setOpen(!restaurant.isOpen());
        return restaurantRepository.save(restaurant);
    }
}
