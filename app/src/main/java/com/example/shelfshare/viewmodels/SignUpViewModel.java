package com.example.shelfshare.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.shelfshare.data.User;
import com.example.shelfshare.repositories.UserRepository;
import com.google.firebase.auth.FirebaseUser;

public class SignUpViewModel extends ViewModel {
    private final UserRepository repository;
    private final MutableLiveData<SignUpState> signUpState = new MutableLiveData<>(SignUpState.IDLE);
    private final MutableLiveData<FirebaseUser> currentUser = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> error = new MutableLiveData<>();

    public SignUpViewModel() {
        repository = new UserRepository();
    }

    public void signUp(String name, String email, String password) {
        isLoading.setValue(true);
        repository.signUp(name, email, password)
            .addOnSuccessListener(authResult -> {
                isLoading.setValue(false);
                signUpState.setValue(SignUpState.SUCCESS);
                currentUser.setValue(authResult.getUser());
            })
            .addOnFailureListener(e -> {
                isLoading.setValue(false);
                errorMessage.setValue(e.getMessage());
                signUpState.setValue(SignUpState.ERROR);
            });
    }

    public LiveData<SignUpState> getSignUpState() {
        return signUpState;
    }

    public LiveData<FirebaseUser> getCurrentUser() {
        return currentUser;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getError() {
        return error;
    }

    public enum SignUpState {
        IDLE, LOADING, SUCCESS, ERROR
    }
} 