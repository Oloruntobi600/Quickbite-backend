package com.quickbite.service;

import com.quickbite.model.Address;
import com.quickbite.model.User;
import com.quickbite.repository.AddressRepository;
import com.quickbite.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AddressService {
    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private UserRepository userRepository;

    public Address saveAddress(Address address) {
        // Save the new address
        return addressRepository.save(address);
    }

    public Address updateAddress(Long addressId, Long userId, Address newAddress) throws Exception {
        // Fetch the user by ID
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new Exception("User not found"));

        // Fetch the address for this user
        Optional<Address> existingAddress = addressRepository.findByIdAndUserId(addressId, userId);

        if (existingAddress.isEmpty()) {
            throw new Exception("Address not found for the user");
        }

        // Proceed with updating the address
        Address addressToUpdate = existingAddress.get();
        addressToUpdate.setStreet(newAddress.getStreet());
        addressToUpdate.setCity(newAddress.getCity());
        addressToUpdate.setState(newAddress.getState());
        addressToUpdate.setZipCode(newAddress.getZipCode());

        return addressRepository.save(addressToUpdate);
    }
}

