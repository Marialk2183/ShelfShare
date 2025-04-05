package com.example.shelfshare;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.shelfshare.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private static final String VALID_EMAIL = "maria@gmail.com";
    private static final String VALID_PASSWORD = "maria123";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Set up click listeners
        binding.btnLogin.setOnClickListener(v -> handleLogin());
        binding.tvSignUp.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }

    private void handleLogin() {
        try {
            String email = binding.etEmail.getText().toString().trim();
            String password = binding.etPassword.getText().toString().trim();

            // Basic validation
            if (email.isEmpty()) {
                binding.etEmail.setError("Email is required");
                return;
            }
            if (password.isEmpty()) {
                binding.etPassword.setError("Password is required");
                return;
            }

            // Check credentials
            if (email.equals(VALID_EMAIL) && password.equals(VALID_PASSWORD)) {
                // Successful login
                Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finishAffinity(); // This will close all activities in the stack
            } else {
                // Invalid credentials
                Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            // Handle any unexpected errors
            Toast.makeText(this, "An error occurred. Please try again.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
}
