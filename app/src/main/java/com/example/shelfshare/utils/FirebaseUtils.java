package com.example.shelfshare.utils;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.Objects;

public class FirebaseUtils {
    private static final String TAG = "FirebaseUtils";
    private static FirebaseUtils instance;
    private static final FirebaseAuth auth = FirebaseAuth.getInstance();
    private static final FirebaseStorage storage = FirebaseStorage.getInstance();
    private final FirebaseFirestore db;

    private FirebaseUtils() {
        db = FirebaseFirestore.getInstance();
    }

    public static synchronized FirebaseUtils getInstance() {
        if (instance == null) {
            instance = new FirebaseUtils();
        }
        return instance;
    }

    public static FirebaseUser getCurrentUser() {
        return auth.getCurrentUser();
    }

    public static String getCurrentUserId() {
        FirebaseUser user = getCurrentUser();
        return user != null ? user.getUid() : null;
    }

    public static Task<Void> updateProfile(String name, String email) {
        FirebaseUser user = getCurrentUser();
        if (user == null) {
            return null;
        }

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build();

        return user.updateProfile(profileUpdates)
                .continueWithTask(task -> {
                    if (task.isSuccessful() && !Objects.equals(user.getEmail(), email)) {
                        return user.updateEmail(email);
                    }
                    return task;
                });
    }

    public static Task<Uri> updateProfileImage(Uri imageUri) {
        FirebaseUser user = getCurrentUser();
        if (user == null) {
            return null;
        }

        StorageReference profileRef = storage.getReference()
                .child("profile_images")
                .child(user.getUid() + ".jpg");

        return profileRef.putFile(imageUri)
                .continueWithTask(task -> {
                    if (!task.isSuccessful()) {
                        throw Objects.requireNonNull(task.getException());
                    }
                    return profileRef.getDownloadUrl();
                })
                .continueWithTask(task -> {
                    if (!task.isSuccessful()) {
                        throw Objects.requireNonNull(task.getException());
                    }
                    Uri downloadUri = task.getResult();
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setPhotoUri(downloadUri)
                            .build();
                    return user.updateProfile(profileUpdates)
                            .continueWith(updateTask -> {
                                if (!updateTask.isSuccessful()) {
                                    throw Objects.requireNonNull(updateTask.getException());
                                }
                                return downloadUri;
                            });
                });
    }

    public static Task<Void> changePassword(String currentPassword, String newPassword) {
        FirebaseUser user = getCurrentUser();
        if (user == null) {
            return null;
        }

        return auth.signInWithEmailAndPassword(user.getEmail(), currentPassword)
                .continueWithTask(task -> {
                    if (!task.isSuccessful()) {
                        throw Objects.requireNonNull(task.getException());
                    }
                    return user.updatePassword(newPassword);
                });
    }

    public Task<AuthResult> signIn(String email, String password) {
        return auth.signInWithEmailAndPassword(email, password);
    }

    public Task<AuthResult> signUp(String email, String password) {
        return auth.createUserWithEmailAndPassword(email, password)
            .continueWithTask(task -> {
                if (task.isSuccessful()) {
                    return task;
                } else {
                    return Tasks.forException(task.getException());
                }
            });
    }

    public Task<Void> signOut() {
        auth.signOut();
        return Tasks.forResult(null);
    }

    public FirebaseFirestore getFirestore() {
        return db;
    }

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

    public Task<DocumentSnapshot> getUserData(String userId) {
        return db.collection("users").document(userId).get();
    }

    public Task<Void> updateUserProfile(String userId, Map<String, Object> updates) {
        return db.collection("users").document(userId).update(updates);
    }

    public FirebaseAuth getAuth() {
        return auth;
    }

    public static class User {
        private String name;
        private String email;

        public User() {}

        public User(String name, String email) {
            this.name = name;
            this.email = email;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
    }
} 