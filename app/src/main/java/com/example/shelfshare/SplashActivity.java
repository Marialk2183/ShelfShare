package com.example.shelfshare;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import androidx.appcompat.app.AppCompatActivity;
import com.example.shelfshare.databinding.ActivitySplashBinding;

public class SplashActivity extends AppCompatActivity {

    private ActivitySplashBinding binding;
    private static final long ANIMATION_DURATION = 1000; // 1 second
    private static final long BUTTON_DELAY = 2000; // 2 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Hide the action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Start animations
        startAnimations();

        // Set up click listeners
        setupClickListeners();
    }

    private void startAnimations() {
        // Fade in animation for app name
        Animation fadeInAppName = new AlphaAnimation(0.0f, 1.0f);
        fadeInAppName.setDuration(ANIMATION_DURATION);
        fadeInAppName.setStartOffset(500);
        binding.tvAppName.startAnimation(fadeInAppName);
        binding.tvAppName.setAlpha(1.0f);

        // Fade in animation for tagline
        Animation fadeInTagline = new AlphaAnimation(0.0f, 1.0f);
        fadeInTagline.setDuration(ANIMATION_DURATION);
        fadeInTagline.setStartOffset(1000);
        binding.tvTagline.startAnimation(fadeInTagline);
        binding.tvTagline.setAlpha(1.0f);

        // Fade in animation for buttons after delay
        new Handler().postDelayed(() -> {
            Animation fadeInButtons = new AlphaAnimation(0.0f, 1.0f);
            fadeInButtons.setDuration(ANIMATION_DURATION);
            binding.buttonContainer.startAnimation(fadeInButtons);
            binding.buttonContainer.setAlpha(1.0f);
        }, BUTTON_DELAY);
    }

    private void setupClickListeners() {
        binding.btnLogin.setOnClickListener(view -> {
            Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        binding.btnSignUp.setOnClickListener(view -> {
            Intent intent = new Intent(SplashActivity.this, SignUpActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }
}
