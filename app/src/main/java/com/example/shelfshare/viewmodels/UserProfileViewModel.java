package com.example.shelfshare.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class UserProfileViewModel extends ViewModel {
    private final MutableLiveData<String> userName = new MutableLiveData<>();
    private final MutableLiveData<String> userEmail = new MutableLiveData<>();
    private final MutableLiveData<String> userLocation = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final FirebaseAuth auth;
    private final FirebaseFirestore db;

    public UserProfileViewModel() {
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        loadUserProfile();
    }

    public LiveData<String> getUserName() {
        return userName;
    }

    public LiveData<String> getUserEmail() {
        return userEmail;
    }

    public LiveData<String> getUserLocation() {
        return userLocation;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getError() {
        return error;
    }

    private void loadUserProfile() {
        isLoading.setValue(true);
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            userEmail.setValue(user.getEmail());
            db.collection("users")
                    .document(user.getUid())
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            userName.setValue(documentSnapshot.getString("name"));
                            userLocation.setValue(documentSnapshot.getString("location"));
                        }
                        isLoading.setValue(false);
                    })
                    .addOnFailureListener(e -> {
                        error.setValue("Failed to load user profile");
                        isLoading.setValue(false);
                    });
        } else {
            error.setValue("No user logged in");
            isLoading.setValue(false);
        }
    }

    public void updateProfile(String name, String location) {
        isLoading.setValue(true);
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            Map<String, Object> updates = new HashMap<>();
            updates.put("name", name);
            updates.put("location", location);

            db.collection("users")
                    .document(user.getUid())
                    .update(updates)
                    .addOnSuccessListener(aVoid -> {
                        userName.setValue(name);
                        userLocation.setValue(location);
                        isLoading.setValue(false);
                    })
                    .addOnFailureListener(e -> {
                        error.setValue("Failed to update profile");
                        isLoading.setValue(false);
                    });
        } else {
            error.setValue("No user logged in");
            isLoading.setValue(false);
        }
    }
} 