package com.umcsuser.carrent.web;

import com.umcsuser.carrent.models.CartItem;
import com.umcsuser.carrent.models.User;
import com.umcsuser.carrent.services.impl.CartService;
import com.umcsuser.carrent.services.UserServiceInterface;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;
    private final UserServiceInterface userService;

    public CartController(CartService cartService, UserServiceInterface userService) {
        this.cartService = cartService;
        this.userService = userService;
    }

    private String getLoggedUserId(UserDetails userDetails) {
        User user = userService.findByLogin(userDetails.getUsername());
        return user.getId();
    }

    @GetMapping
    public List<CartItem> getCart(@AuthenticationPrincipal UserDetails userDetails) {
        return cartService.getUserCart(getLoggedUserId(userDetails));
    }

    @PostMapping("/add/{bookId}")
    public CartItem add(@AuthenticationPrincipal UserDetails userDetails, @PathVariable String bookId) {
        return cartService.addToCart(getLoggedUserId(userDetails), bookId);
    }

    @DeleteMapping("/remove/{bookId}")
    public ResponseEntity<Void> remove(@AuthenticationPrincipal UserDetails userDetails, @PathVariable String bookId) {
        cartService.removeFromCart(getLoggedUserId(userDetails), bookId);
        return ResponseEntity.noContent().build();
    }
}//a