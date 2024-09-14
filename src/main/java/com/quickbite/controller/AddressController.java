package com.quickbite.controller;

import com.quickbite.model.Address;
import com.quickbite.model.User;
import com.quickbite.repository.AddressRepository;
import com.quickbite.service.AddressService;
import com.quickbite.service.UserService;
import com.quickbite.service.UserServiceImp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/address")
public class AddressController {

    @Autowired
    private AddressService addressService;

    @Autowired
    private UserService userService;

    @Autowired
    private AddressRepository addressRepository;


    @PostMapping
    public ResponseEntity<Address> createAddress(@RequestBody Address address, @RequestHeader("Authorization") String token) {
        try {
            // Extract JWT token from Authorization header
            String jwtToken = token.replace("Bearer ", "");

            // Find user by JWT token
            User user = userService.findUserByJwtToken(jwtToken);

            // Set user to the address
            address.setUser(user);

            // Save address
            Address createdAddress = addressService.saveAddress(address);

            return ResponseEntity.ok(createdAddress);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }


    @PutMapping("/{id}")
    public ResponseEntity<Address> updateAddress(@PathVariable Long id, @RequestBody Address newAddress) {


        Optional<Address> existingAddress = addressRepository.findById(id);

        if (!existingAddress.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(null); // Or a meaningful error message
        }

        // Proceed with updating the existing address
        Address updatedAddress = existingAddress.get();
        updatedAddress.setStreet(newAddress.getStreet());
        updatedAddress.setCity(newAddress.getCity());
        updatedAddress.setState(newAddress.getState());
        updatedAddress.setZipCode(newAddress.getZipCode());
        updatedAddress.setCountry(newAddress.getCountry());
        updatedAddress.setUser(newAddress.getUser());
        // Update other fields...

        addressRepository.save(updatedAddress);

        return ResponseEntity.ok(updatedAddress);
    }
}