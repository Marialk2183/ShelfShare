package com.example.shelfshare.models;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.IgnoreExtraProperties;
import java.util.Date;

@IgnoreExtraProperties
public class Rental {
    private String id;
    private String bookId;
    private String userId;
    private Date startDate;
    private Date endDate;
    private double totalAmount;
    private String status;
    private Book book;
    private double lateFee;

    public Rental() {
        // Default constructor required for Firestore
    }

    public Rental(String bookId, String userId, Date startDate, Date endDate, double totalAmount) {
        this.bookId = bookId;
        this.userId = userId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.totalAmount = totalAmount;
        this.status = "PENDING";
    }

    @Exclude
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