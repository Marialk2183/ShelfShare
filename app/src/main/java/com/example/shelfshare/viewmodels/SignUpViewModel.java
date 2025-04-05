package com.example.shelfshare.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.shelfshare.utils.FirebaseUtils;
import com.google.firebase.auth.FirebaseUser;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class SignUpViewModel extends ViewModel {
    private final FirebaseUtils firebaseUtils;
    private final MutableLiveData<SignUpState> signUpState = new MutableLiveData<>();
    private final MutableLiveData<FirebaseUser> currentUser = new MutableLiveData<>();
    private String errorMessage = "";

    public enum SignUpState {
        IDLE,
        LOADING,
        SUCCESS,
        ERROR
    }

    public SignUpViewModel() {
        firebaseUtils = FirebaseUtils.getInstance();
        signUpState.setValue(SignUpState.IDLE);
    }

    public LiveData<SignUpState> getSignUpState() {
        return signUpState;
    }

    public LiveData<FirebaseUser> getCurrentUser() {
        return currentUser;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void signUp(String name, String email, String password) {
        signUpState.setValue(SignUpState.LOADING);
        
        firebaseUtils.signUp(email, password, name)
                .thenAccept(success -> {
                    if (success) {
                        FirebaseUser user = firebaseUtils.getCurrentUser();
                        if (user != null) {
                            // Create user profile in Firestore
                            Map<String, Object> userData = new HashMap<>();
                            userData.put("name", name);
                            userData.put("email", email);
                            userData.put("createdAt", System.currentTimeMillis());
                            userData.put("location", ""); // Default empty location
                            userData.put("profileImage", ""); // Default empty profile image

                            firebaseUtils.getFirestore()
                                    .collection("users")
                                    .document(user.getUid())
                                    .set(userData)
                                    .addOnSuccessListener(aVoid -> {
                                        currentUser.postValue(user);
                                        signUpState.postValue(SignUpState.SUCCESS);
                                    })
                                    .addOnFailureListener(e -> {
                                        errorMessage = "Error creating user profile: " + e.getMessage();
                                        signUpState.postValue(SignUpState.ERROR);
                                    });
                        } else {
                            errorMessage = "User not found";
                            signUpState.postValue(SignUpState.ERROR);
                        }
                    } else {
                        errorMessage = "Sign up failed";
                        signUpState.postValue(SignUpState.ERROR);
                    }
                })
                .exceptionally(throwable -> {
                    errorMessage = throwable.getMessage();
                    signUpState.postValue(SignUpState.ERROR);
                    return null;
                });
    }
} 