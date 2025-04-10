package com.example.shelfshare.viewmodels;

import android.net.Uri;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.shelfshare.data.User;
import com.example.shelfshare.utils.FirebaseUtils;
import com.google.firebase.auth.FirebaseUser;

public class UserProfileViewModel extends ViewModel {
    private final MutableLiveData<User> user = new MutableLiveData<>();
    private final MutableLiveData<Uri> profileImage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<Boolean> success = new MutableLiveData<>();

    public UserProfileViewModel() {
        loadUserData();
    }

    private void loadUserData() {
        isLoading.setValue(true);
        FirebaseUser firebaseUser = FirebaseUtils.getCurrentUser();
        if (firebaseUser != null) {
            User userData = new User();
            userData.setName(firebaseUser.getDisplayName());
            userData.setEmail(firebaseUser.getEmail());
            user.setValue(userData);

            if (firebaseUser.getPhotoUrl() != null) {
                profileImage.setValue(firebaseUser.getPhotoUrl());
            }
        }
        isLoading.setValue(false);
    }

    public void updateProfile(String name, String email) {
        isLoading.setValue(true);
        FirebaseUtils.updateProfile(name, email)
                .addOnSuccessListener(aVoid -> {
                    User updatedUser = new User();
                    updatedUser.setName(name);
                    updatedUser.setEmail(email);
                    user.setValue(updatedUser);
                    success.setValue(true);
                    isLoading.setValue(false);
                })
                .addOnFailureListener(e -> {
                    error.setValue(e.getMessage());
                    isLoading.setValue(false);
                });
    }

    public void updateProfileImage(Uri imageUri) {
        isLoading.setValue(true);
        FirebaseUtils.updateProfileImage(imageUri)
                .addOnSuccessListener(uri -> {
                    profileImage.setValue(uri);
                    success.setValue(true);
                    isLoading.setValue(false);
                })
                .addOnFailureListener(e -> {
                    error.setValue(e.getMessage());
                    isLoading.setValue(false);
                });
    }

    public void changePassword(String currentPassword, String newPassword) {
        isLoading.setValue(true);
        FirebaseUtils.getInstance().changePassword(currentPassword, newPassword)
            .addOnSuccessListener(aVoid -> {
                isLoading.setValue(false);
                error.setValue(null);
            })
            .addOnFailureListener(e -> {
                isLoading.setValue(false);
                error.setValue(e.getMessage());
            });
    }

    public LiveData<User> getUser() {
        return user;
    }

    public LiveData<Uri> getProfileImage() {
        return profileImage;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getError() {
        return error;
    }

    public LiveData<Boolean> getSuccess() {
        return success;
    }
} 