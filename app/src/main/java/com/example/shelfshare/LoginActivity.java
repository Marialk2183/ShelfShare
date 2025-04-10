package com.example.shelfshare;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.shelfshare.databinding.ActivityLoginBinding;
import com.example.shelfshare.viewmodels.LoginViewModel;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;
    private LoginViewModel loginViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        setupObservers();
        setupClickListeners();
    }

    private void setupObservers() {
        loginViewModel.getLoginState().observe(this, state -> {
            switch (state) {
                case LOADING:
                    showLoading(true);
                    break;
                case SUCCESS:
                    showLoading(false);
                    navigateToMain();
                    break;
                case ERROR:
                    showLoading(false);
                    break;
                case IDLE:
                    showLoading(false);
                    break;
            }
        });

        loginViewModel.getErrorMessage().observe(this, errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                showError(errorMessage);
            }
        });
    }

    private void setupClickListeners() {
        binding.btnLogin.setOnClickListener(v -> {
            String email = binding.etEmail.getText().toString().trim();
            String password = binding.etPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                showError("Please fill in all fields");
                return;
            }

            loginViewModel.login(email, password)
                .addOnSuccessListener(authResult -> {
                    // Success is handled by the LiveData observer
                })
                .addOnFailureListener(e -> {
                    showError(e.getMessage());
                });
        });

        binding.tvSignUp.setOnClickListener(v -> {
            startActivity(new Intent(this, SignUpActivity.class));
            finish();
        });
    }

    private void showLoading(boolean isLoading) {
        binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        binding.btnLogin.setEnabled(!isLoading);
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void navigateToMain() {
        startActivity(new Intent(this, DashboardActivity.class));
        finish();
    }
}
