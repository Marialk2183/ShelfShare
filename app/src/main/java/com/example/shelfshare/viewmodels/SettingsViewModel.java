package com.example.shelfshare.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SettingsViewModel extends ViewModel {
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<Boolean> logoutSuccess = new MutableLiveData<>(false);
    private final FirebaseAuth auth;
    private final MutableLiveData<Boolean> notificationsEnabled = new MutableLiveData<>(true);
    private final MutableLiveData<Boolean> darkModeEnabled = new MutableLiveData<>(false);
    private final MutableLiveData<String> appVersion = new MutableLiveData<>("1.0.0");

    public SettingsViewModel() {
        auth = FirebaseAuth.getInstance();
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getError() {
        return error;
    }

    public LiveData<Boolean> getLogoutSuccess() {
        return logoutSuccess;
    }

    public void logout() {
        isLoading.setValue(true);
        auth.signOut();
        logoutSuccess.setValue(true);
        isLoading.setValue(false);
    }

    public void clearCache() {
        isLoading.setValue(true);
        // Implement cache clearing logic here
        isLoading.setValue(false);
    }

    public void deleteAccount() {
        isLoading.setValue(true);
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            user.delete()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            logoutSuccess.setValue(true);
                        } else {
                            error.setValue("Failed to delete account");
                        }
                        isLoading.setValue(false);
                    });
        } else {
            error.setValue("No user logged in");
            isLoading.setValue(false);
        }
    }

    public void loadSettings() {
        // Load settings from SharedPreferences or other storage
        // For now, we'll just use the default values
    }

    public void setNotificationsEnabled(boolean enabled) {
        notificationsEnabled.setValue(enabled);
        // Save to SharedPreferences or other storage
    }

    public void setDarkModeEnabled(boolean enabled) {
        darkModeEnabled.setValue(enabled);
        // Save to SharedPreferences or other storage
    }

    public LiveData<Boolean> getNotificationsEnabled() {
        return notificationsEnabled;
    }

    public LiveData<Boolean> getDarkModeEnabled() {
        return darkModeEnabled;
    }

    public LiveData<String> getAppVersion() {
        return appVersion;
    }
} 