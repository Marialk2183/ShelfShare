package com.example.shelfshare.utils;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class FirebaseUtils {
    private static final String TAG = "FirebaseUtils";
    private static FirebaseUtils instance;
    private final FirebaseAuth mAuth;
    private final FirebaseFirestore db;
    private final FirebaseStorage storage;

    private FirebaseUtils() {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
    }

    public static synchronized FirebaseUtils getInstance() {
        if (instance == null) {
            instance = new FirebaseUtils();
        }
        return instance;
    }

    // Authentication Methods
    public FirebaseUser getCurrentUser() {
        return mAuth.getCurrentUser();
    }

    public CompletableFuture<Boolean> signIn(String email, String password) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        future.complete(true);
                    } else {
                        Log.e(TAG, "Sign in failed", task.getException());
                        future.complete(false);
                    }
                });
        return future;
    }

    public CompletableFuture<Boolean> signUp(String email, String password, String name) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            // Create user profile in Firestore
                            Map<String, Object> userData = new HashMap<>();
                            userData.put("name", name);
                            userData.put("email", email);
                            userData.put("createdAt", System.currentTimeMillis());

                            db.collection("users").document(user.getUid())
                                    .set(userData)
                                    .addOnSuccessListener(aVoid -> future.complete(true))
                                    .addOnFailureListener(e -> {
                                        Log.e(TAG, "Error creating user profile", e);
                                        future.complete(false);
                                    });
                        }
                    } else {
                        Log.e(TAG, "Sign up failed", task.getException());
                        future.complete(false);
                    }
                });
        return future;
    }

    public void signOut() {
        mAuth.signOut();
    }

    // Firestore Methods
    public FirebaseFirestore getFirestore() {
        return db;
    }

    // Storage Methods
    public StorageReference getStorageReference() {
        return storage.getReference();
    }

    public CompletableFuture<String> uploadImage(Uri imageUri, String path) {
        CompletableFuture<String> future = new CompletableFuture<>();
        StorageReference storageRef = storage.getReference().child(path);
        
        storageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    storageRef.getDownloadUrl()
                            .addOnSuccessListener(uri -> future.complete(uri.toString()))
                            .addOnFailureListener(e -> {
                                Log.e(TAG, "Error getting download URL", e);
                                future.completeExceptionally(e);
                            });
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error uploading image", e);
                    future.completeExceptionally(e);
                });
        
        return future;
    }
} 