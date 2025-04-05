package com.example.shelfshare.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.shelfshare.models.Book;
import com.example.shelfshare.utils.FavoritesManager;
import com.example.shelfshare.utils.FirebaseUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class FavoritesViewModel extends ViewModel {
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final FavoritesManager favoritesManager;
    private final FirebaseFirestore firestore;

    public FavoritesViewModel() {
        favoritesManager = FavoritesManager.getInstance();
        firestore = FirebaseUtils.getInstance().getFirestore();
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getError() {
        return error;
    }

    public List<Book> getFavoriteBooks() {
        return favoritesManager.getFavoriteBooks();
    }

    public void addToFavorites(Book book) {
        isLoading.setValue(true);
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        firestore.collection("users")
                .document(userId)
                .collection("favorites")
                .document(book.getId())
                .set(book)
                .addOnSuccessListener(aVoid -> {
                    favoritesManager.addToFavorites(book);
                    isLoading.setValue(false);
                })
                .addOnFailureListener(e -> {
                    error.setValue("Failed to add to favorites: " + e.getMessage());
                    isLoading.setValue(false);
                });
    }

    public void removeFromFavorites(Book book) {
        isLoading.setValue(true);
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        firestore.collection("users")
                .document(userId)
                .collection("favorites")
                .document(book.getId())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    favoritesManager.removeFromFavorites(book);
                    isLoading.setValue(false);
                })
                .addOnFailureListener(e -> {
                    error.setValue("Failed to remove from favorites: " + e.getMessage());
                    isLoading.setValue(false);
                });
    }

    public boolean isFavorite(Book book) {
        return favoritesManager.isFavorite(book);
    }
} 