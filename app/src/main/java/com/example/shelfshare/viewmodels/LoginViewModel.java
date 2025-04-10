package com.example.shelfshare.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.shelfshare.utils.FirebaseUtils;
import com.google.firebase.auth.FirebaseUser;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;

public class LoginViewModel extends ViewModel {
    private final FirebaseUtils firebaseUtils;
    private final MutableLiveData<LoginState> loginState = new MutableLiveData<>(LoginState.IDLE);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public enum LoginState {
        IDLE,
        LOADING,
        SUCCESS,
        ERROR
    }

    public LoginViewModel() {
        firebaseUtils = FirebaseUtils.getInstance();
    }

    public LiveData<LoginState> getLoginState() {
        return loginState;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public Task<AuthResult> login(String email, String password) {
        loginState.setValue(LoginState.LOADING);
        return firebaseUtils.signIn(email, password)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser user = authResult.getUser();
                    if (user != null) {
                        loginState.setValue(LoginState.SUCCESS);
                    } else {
                        errorMessage.setValue("User not found");
                        loginState.setValue(LoginState.ERROR);
                    }
                })
                .addOnFailureListener(e -> {
                    errorMessage.setValue(e.getMessage());
                    loginState.setValue(LoginState.ERROR);
                });
    }

    public void signOut() {
        firebaseUtils.signOut();
        loginState.setValue(LoginState.IDLE);
    }
} 