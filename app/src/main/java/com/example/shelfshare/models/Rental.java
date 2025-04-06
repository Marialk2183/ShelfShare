package com.example.shelfshare.models;

import com.google.firebase.firestore.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Rental {
    private String id;
    private String bookId;
    private String userId;
    private long rentalDate;
    private long returnDate;
    private String status;
    private double totalAmount;
    private String bookTitle;
    private String bookImageUrl;

    public Rental() {
        // Default constructor required for Firestore
    }

    public Rental(String id, String bookId, String userId, long rentalDate, long returnDate, 
                 String status, double totalAmount, String bookTitle, String bookImageUrl) {
        this.id = id;
        this.bookId = bookId;
        this.userId = userId;
        this.rentalDate = rentalDate;
        this.returnDate = returnDate;
        this.status = status;
        this.totalAmount = totalAmount;
        this.bookTitle = bookTitle;
        this.bookImageUrl = bookImageUrl;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public long getRentalDate() {
        return rentalDate;
    }

    public void setRentalDate(long rentalDate) {
        this.rentalDate = rentalDate;
    }

    public long getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(long returnDate) {
        this.returnDate = returnDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public String getBookImageUrl() {
        return bookImageUrl;
    }

    public void setBookImageUrl(String bookImageUrl) {
        this.bookImageUrl = bookImageUrl;
    }
} 