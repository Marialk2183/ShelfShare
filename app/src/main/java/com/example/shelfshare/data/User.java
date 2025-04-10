package com.example.shelfshare.data;

import java.util.Date;

public class User {
    private String id;
    private String name;
    private String email;
    private String phone;
    private String profileImageUrl;
    private Date createdAt;
    private Date lastLogin;
    private String location;

    public User() {
        // Required for Firestore
    }

    public User(String name, String email) {
        this.name = name;
        this.email = email;
        this.location = "";
        this.createdAt = new Date();
        this.lastLogin = new Date();
    }

    public User(String name, String email, String location) {
        this.name = name;
        this.email = email;
        this.location = location;
        this.createdAt = new Date();
        this.lastLogin = new Date();
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(Date lastLogin) {
        this.lastLogin = lastLogin;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
} 