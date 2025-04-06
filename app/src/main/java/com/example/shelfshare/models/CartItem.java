package com.example.shelfshare.models;

import com.google.firebase.firestore.Exclude;

public class CartItem {
    private Book book;
    private int quantity;

    // Default constructor required for Firestore
    public CartItem() {}

    public CartItem(Book book, int quantity) {
        this.book = book;
        this.quantity = quantity;
    }

    @Exclude
    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Exclude
    public double getTotalPrice() {
        return book.getPrice() * quantity;
    }
} 