package com.umcsuser.carrent.repositories;

import com.umcsuser.carrent.models.CartItem;
import java.util.List;
import java.util.Optional;

public interface CartItemRepository {
    List<CartItem> findByUserId(String userId);
    Optional<CartItem> findByUserIdAndBookId(String userId, String bookId);
    CartItem save(CartItem cartItem);
    void deleteById(String id);
    void deleteByUserId(String userId);
}