package com.example.shelfshare.models;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.IgnoreExtraProperties;
import java.util.Date;

@IgnoreExtraProperties
public class Rental {
    private String id;
    private String title;
    private String author;
    private String imageUrl;
    private String dueDate;
    private String userId;
    private Date startDate;
    private Date endDate;
    private double totalAmount;
    private String status;
    private Book book;
    private double lateFee;

    public Rental() {
        // Required empty constructor for Firestore
    }

    public Rental(String id, String title, String author, String imageUrl, String dueDate, String userId) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.imageUrl = imageUrl;
        this.dueDate = dueDate;
        this.userId = userId;
    }

    @Exclude
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Exclude
    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    @Exclude
    public double calculateLateFee() {
        if (status.equals("RETURNED")) {
            Date returnDate = new Date();
            if (returnDate.after(endDate)) {
                long diff = returnDate.getTime() - endDate.getTime();
                long days = diff / (24 * 60 * 60 * 1000);
                lateFee = days * book.getPrice();
                return lateFee;
            }
        }
        return 0;
    }
} 