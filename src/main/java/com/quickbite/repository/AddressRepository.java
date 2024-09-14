package com.quickbite.repository;

import com.quickbite.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AddressRepository extends JpaRepository<Address, Long> {
    Optional<Address> findByIdAndUserId(Long addressId, Long userId);

}
