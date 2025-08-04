package com.example.shelfshare.models;

import com.google.firebase.firestore.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Category {
    private String id;
    private String name;
    private String imageUrl;
    private int bookCount;

    public Category() {
        // Default constructor required for Firestore
    }

    public Category(String name, String imageUrl) {
        this.name = name;
        this.imageUrl = imageUrl;
        this.bookCount = 0;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getBookCount() {
        return bookCount;
    }

    public void setBookCount(int bookCount) {
        this.bookCount = bookCount;
    }
} 