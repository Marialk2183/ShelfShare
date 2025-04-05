package com.example.shelfshare.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.shelfshare.utils.FirebaseUtils;
import com.google.firebase.auth.FirebaseUser;

import java.util.concurrent.CompletableFuture;

public class LoginViewModel extends ViewModel {
    private final FirebaseUtils firebaseUtils;
    private final MutableLiveData<LoginState> loginState = new MutableLiveData<>();
    private final MutableLiveData<FirebaseUser> currentUser = new MutableLiveData<>();
    private String errorMessage = "";

    public enum LoginState {
        IDLE,
        LOADING,
        SUCCESS,
        ERROR
    }

    public LoginViewModel() {
        firebaseUtils = FirebaseUtils.getInstance();
        loginState.setValue(LoginState.IDLE);
    }

    public LiveData<LoginState> getLoginState() {
        return loginState;
    }

    public LiveData<FirebaseUser> getCurrentUser() {
        return currentUser;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void checkCurrentUser() {
        FirebaseUser user = firebaseUtils.getCurrentUser();
        if (user != null) {
            currentUser.setValue(user);
        }
    }

    public void login(String email, String password) {
        loginState.setValue(LoginState.LOADING);
        
        firebaseUtils.signIn(email, password)
                .thenAccept(success -> {
                    if (success) {
                        FirebaseUser user = firebaseUtils.getCurrentUser();
                        if (user != null) {
                            currentUser.postValue(user);
                            loginState.postValue(LoginState.SUCCESS);
                        } else {
                            errorMessage = "User not found";
                            loginState.postValue(LoginState.ERROR);
                        }
                    } else {
                        errorMessage = "Invalid email or password";
                        loginState.postValue(LoginState.ERROR);
                    }
                })
                .exceptionally(throwable -> {
                    errorMessage = throwable.getMessage();
                    loginState.postValue(LoginState.ERROR);
                    return null;
                });
    }

    public void signOut() {
        firebaseUtils.signOut();
        currentUser.setValue(null);
        loginState.setValue(LoginState.IDLE);
    }
} 