package com.umcsuser.carrent.models;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class CartItem {
    private String id;
    private String userId;
    private String bookId;
    private int quantity;

    private Book book;
}