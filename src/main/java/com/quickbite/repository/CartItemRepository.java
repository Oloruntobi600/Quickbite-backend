package com.quickbite.repository;

import com.quickbite.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    void deleteByFoodId(Long foodId);
}
