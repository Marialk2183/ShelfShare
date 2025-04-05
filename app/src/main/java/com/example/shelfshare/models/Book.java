package com.example.shelfshare.models;

public class Book {
    private String id;
    private String title;
    private String author;
    private String location;
    private double price;
    private boolean isAvailable;
    private String imageUrl;

    public Book(String id, String title, String author, String location, double price, boolean isAvailable, String imageUrl) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.location = location;
        this.price = price;
        this.isAvailable = isAvailable;
        this.imageUrl = imageUrl;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public boolean isAvailable() { return isAvailable; }
    public void setAvailable(boolean available) { isAvailable = available; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
} 