package com.example.shelfshare.repositories;

import com.example.shelfshare.data.User;
import com.example.shelfshare.utils.FirebaseUtils;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.firestore.DocumentReference;

public class UserRepository {
    private final FirebaseUtils firebaseUtils;

    public UserRepository() {
        firebaseUtils = FirebaseUtils.getInstance();
    }

    public Task<AuthResult> signUp(String name, String email, String password) {
        return firebaseUtils.signUp(email, password)
            .continueWithTask(task -> {
                if (task.isSuccessful()) {
                    String userId = task.getResult().getUser().getUid();
                    User user = new User(name, email);
                    return firebaseUtils.getFirestore()
                        .collection("users")
                        .document(userId)
                        .set(user)
                        .continueWith(ignored -> task.getResult());
                }
                return task;
            });
    }

    public Task<Void> updateProfile(String userId, String name, String email) {
        User user = new User(name, email);
        return firebaseUtils.getFirestore()
            .collection("users")
            .document(userId)
            .set(user);
    }

    public Task<Void> changePassword(String userId, String newPassword) {
        return firebaseUtils.getAuth()
            .getCurrentUser()
            .updatePassword(newPassword);
    }

    public Task<DocumentReference> getUser(String userId) {
        return firebaseUtils.getFirestore()
            .collection("users")
            .document(userId)
            .get()
            .continueWith(task -> {
                if (task.isSuccessful()) {
                    return task.getResult().getReference();
                }
                throw task.getException();
            });
    }
} 