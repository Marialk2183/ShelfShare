package com.example.shelfshare.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.shelfshare.utils.FirebaseUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileViewModel extends ViewModel {
    private final MutableLiveData<String> userName = new MutableLiveData<>();
    private final MutableLiveData<String> userEmail = new MutableLiveData<>();
    private final MutableLiveData<String> userLocation = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final FirebaseFirestore firestore;

    public ProfileViewModel() {
        firestore = FirebaseUtils.getInstance().getFirestore();
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
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            error.setValue("User not logged in");
            isLoading.setValue(false);
            return;
        }

        userEmail.setValue(currentUser.getEmail());

        firestore.collection("users")
                .document(currentUser.getUid())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        userName.setValue(documentSnapshot.getString("name"));
                        userLocation.setValue(documentSnapshot.getString("location"));
                    }
                    isLoading.setValue(false);
                })
                .addOnFailureListener(e -> {
                    error.setValue("Failed to load user profile: " + e.getMessage());
                    isLoading.setValue(false);
                });
    }

    public void updateProfile(String name, String location) {
        isLoading.setValue(true);
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            error.setValue("User not logged in");
            isLoading.setValue(false);
            return;
        }

        firestore.collection("users")
                .document(currentUser.getUid())
                .update("name", name, "location", location)
                .addOnSuccessListener(aVoid -> {
                    userName.setValue(name);
                    userLocation.setValue(location);
                    isLoading.setValue(false);
                })
                .addOnFailureListener(e -> {
                    error.setValue("Failed to update profile: " + e.getMessage());
                    isLoading.setValue(false);
                });
    }

    public void changePassword(String currentPassword, String newPassword) {
        isLoading.setValue(true);
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            error.setValue("User not logged in");
            isLoading.setValue(false);
            return;
        }

        // Reauthenticate user
        currentUser.reauthenticate(com.google.firebase.auth.EmailAuthProvider.getCredential(
                currentUser.getEmail(), currentPassword))
                .addOnSuccessListener(aVoid -> {
                    // Update password
                    currentUser.updatePassword(newPassword)
                            .addOnSuccessListener(aVoid1 -> {
                                isLoading.setValue(false);
                            })
                            .addOnFailureListener(e -> {
                                error.setValue("Failed to change password: " + e.getMessage());
                                isLoading.setValue(false);
                            });
                })
                .addOnFailureListener(e -> {
                    error.setValue("Current password is incorrect");
                    isLoading.setValue(false);
                });
    }
} 