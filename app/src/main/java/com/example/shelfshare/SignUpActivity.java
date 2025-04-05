package com.example.shelfshare;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.shelfshare.databinding.ActivitySignupBinding;
import com.example.shelfshare.viewmodels.SignUpViewModel;
import com.google.firebase.auth.FirebaseUser;

public class SignUpActivity extends AppCompatActivity {
    private ActivitySignupBinding binding;
    private SignUpViewModel signUpViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize ViewModel
        signUpViewModel = new ViewModelProvider(this).get(SignUpViewModel.class);

        // Set up observers
        setupObservers();

        // Set up click listeners
        binding.btnSignUp.setOnClickListener(v -> handleSignUp());
        binding.tvLogin.setOnClickListener(v -> navigateToLogin());
    }

    private void setupObservers() {
        // Observe sign up state
        signUpViewModel.getSignUpState().observe(this, signUpState -> {
            switch (signUpState) {
                case LOADING:
                    showLoading(true);
                    break;
                case SUCCESS:
                    showLoading(false);
                    navigateToDashboard();
                    break;
                case ERROR:
                    showLoading(false);
                    showError(signUpViewModel.getErrorMessage());
                    break;
            }
        });

        // Observe current user
        signUpViewModel.getCurrentUser().observe(this, this::handleUserState);
    }

    private void handleSignUp() {
        String name = binding.etName.getText().toString().trim();
        String email = binding.etEmail.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();
        String confirmPassword = binding.etConfirmPassword.getText().toString().trim();

        if (validateInputs(name, email, password, confirmPassword)) {
            signUpViewModel.signUp(name, email, password);
        }
    }

    private boolean validateInputs(String name, String email, String password, String confirmPassword) {
        if (TextUtils.isEmpty(name)) {
            binding.etName.setError("Name is required");
            return false;
        }

        if (TextUtils.isEmpty(email)) {
            binding.etEmail.setError("Email is required");
            return false;
        }

        if (TextUtils.isEmpty(password)) {
            binding.etPassword.setError("Password is required");
            return false;
        }

        if (password.length() < 6) {
            binding.etPassword.setError("Password must be at least 6 characters");
            return false;
        }

        if (TextUtils.isEmpty(confirmPassword)) {
            binding.etConfirmPassword.setError("Please confirm your password");
            return false;
        }

        if (!password.equals(confirmPassword)) {
            binding.etConfirmPassword.setError("Passwords do not match");
            return false;
        }

        return true;
    }

    private void handleUserState(FirebaseUser user) {
        if (user != null) {
            navigateToDashboard();
        }
    }

    private void navigateToDashboard() {
        Intent intent = new Intent(SignUpActivity.this, DashboardActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void navigateToLogin() {
        startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
        finish();
    }

    private void showLoading(boolean isLoading) {
        binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        binding.btnSignUp.setEnabled(!isLoading);
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
