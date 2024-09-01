package com.quickbite.service;

import com.quickbite.dto.RestaurantDto;
import com.quickbite.model.Address;
import com.quickbite.model.Restaurant;
import com.quickbite.model.User;
import com.quickbite.repository.AddressRepository;
import com.quickbite.repository.RestaurantRepository;
import com.quickbite.repository.UserRepository;
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

        Restaurant restaurant = findRestaurantById(restaurantId);

        if(restaurant.getCuisineType()!=null){
            restaurant.setCuisineType(updatedRestaurant.getCuisineType());
        }
        if(restaurant.getDescription()!=null){
            restaurant.setDescription(updatedRestaurant.getDescription());
        }
        if(restaurant.getName()!=null){
            restaurant.setName(updatedRestaurant.getName());
        }

        return restaurantRepository.save(restaurant);
    }

    @Override
    @Transactional
    public void deleteRestaurant(Long restaurantId) throws Exception {
        Restaurant restaurant = findRestaurantById(restaurantId);
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
        return restaurantRepository.findById(id)
                .orElseThrow(() -> new Exception("Restaurant not found with id " + id));
    }

    @Override
    public Restaurant getRestaurantByUserId(Long userId) throws Exception {
        return null;
    }

    //    @Override
//    public Restaurant getRestaurantByUserId(Long userId) throws Exception {
//        return Optional.ofNullable(restaurantRepository.findByOwnerId(userId))
//                .orElseThrow(() -> new Exception("Restaurant not found with owner id " + userId));
//    }
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

//        boolean isFavorited = false;
//        List<RestaurantDto> favorites = user.getFavorites();
//        for (RestaurantDto favorite : favorites)
//            if (favorite.getId().equals(restaurantId)){
//                isFavorited = true;
//                break;
//            }
//
//        if (isFavorited){
//            favorites.removeIf(favorite -> favorite.getId().equals(restaurantId));
//        }else {
//            favorites.add(dto);
//        }
//
//        userRepository.save(user);
//        return dto;
//    }

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
