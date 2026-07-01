package com.umcsuser.carrent.services.impl;

import com.umcsuser.carrent.models.Book;
import com.umcsuser.carrent.models.CartItem;
import com.umcsuser.carrent.repositories.BookRepository;
import com.umcsuser.carrent.repositories.CartItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CartService {

    private final CartItemRepository cartRepository;
    private final BookRepository bookRepository;

    public CartService(CartItemRepository cartRepository, BookRepository bookRepository) {
        this.cartRepository = cartRepository;
        this.bookRepository = bookRepository;
    }

    @Transactional(readOnly = true)
    public List<CartItem> getUserCart(String userId) {
        List<CartItem> items = cartRepository.findByUserId(userId);
        for (CartItem item : items) {
            bookRepository.findById(item.getBookId()).ifPresent(item::setBook);
        }
        return items;
    }

    public CartItem addToCart(String userId, String bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono książki o ID: " + bookId));

        Optional<CartItem> existingItem = cartRepository.findByUserIdAndBookId(userId, bookId);

        CartItem item;
        if (existingItem.isPresent()) {
            item = existingItem.get();
            item.setQuantity(item.getQuantity() + 1);
        } else {
            item = CartItem.builder()
                    .userId(userId)
                    .bookId(bookId)
                    .quantity(1)
                    .build();
        }

        CartItem savedItem = cartRepository.save(item);
        savedItem.setBook(book);
        return savedItem;
    }

    public void removeFromCart(String userId, String bookId) {
        Optional<CartItem> existingItem = cartRepository.findByUserIdAndBookId(userId, bookId);

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();

            if (item.getQuantity() > 1) {
                item.setQuantity(item.getQuantity() - 1);
                cartRepository.save(item);
            } else {
                cartRepository.deleteById(item.getId());
            }
        }
    }
}