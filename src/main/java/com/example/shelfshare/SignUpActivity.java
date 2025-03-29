package com.example.shelfshare;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.shelfshare.databinding.ActivitySignupBinding;

public class SignUpActivity extends AppCompatActivity {

    private ActivitySignupBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Set up click listeners
        binding.btnSignUp.setOnClickListener(v -> handleSignUp());
        binding.tvLogin.setOnClickListener(v -> {
            Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void handleSignUp() {
        String name = binding.etName.getText().toString().trim();
        String email = binding.etEmail.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();
        String confirmPassword = binding.etConfirmPassword.getText().toString().trim();

        // Basic validation
        if (name.isEmpty()) {
            binding.etName.setError("Name is required");
            return;
        }
        if (email.isEmpty()) {
            binding.etEmail.setError("Email is required");
            return;
        }
        if (password.isEmpty()) {
            binding.etPassword.setError("Password is required");
            return;
        }
        if (confirmPassword.isEmpty()) {
            binding.etConfirmPassword.setError("Please confirm your password");
            return;
        }
        if (!password.equals(confirmPassword)) {
            binding.etConfirmPassword.setError("Passwords do not match");
            return;
        }

        // TODO: Implement actual registration logic here
        // For now, we'll navigate to DashboardActivity
        Intent intent = new Intent(SignUpActivity.this, DashboardActivity.class);
        startActivity(intent);
        finish();
    }
}
