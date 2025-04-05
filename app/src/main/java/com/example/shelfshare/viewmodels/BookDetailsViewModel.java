package com.example.shelfshare.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.shelfshare.models.Book;
import com.example.shelfshare.utils.FavoritesManager;
import com.example.shelfshare.utils.FirebaseUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.UUID;
import java.util.HashMap;
import java.util.Map;

public class BookDetailsViewModel extends ViewModel {
    private final MutableLiveData<Book> book = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> isFavorite = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> rentalSuccess = new MutableLiveData<>(false);
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final FavoritesManager favoritesManager;
    private final FirebaseFirestore db;

    public BookDetailsViewModel() {
        favoritesManager = FavoritesManager.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    public LiveData<Book> getBook() {
        return book;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<Boolean> getIsFavorite() {
        return isFavorite;
    }

    public LiveData<Boolean> getRentalSuccess() {
        return rentalSuccess;
    }

    public LiveData<String> getError() {
        return error;
    }

    public void loadBookDetails(String bookId) {
        isLoading.setValue(true);
        db.collection("books").document(bookId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Book loadedBook = documentSnapshot.toObject(Book.class);
                        if (loadedBook != null) {
                            loadedBook.setId(documentSnapshot.getId());
                            book.setValue(loadedBook);
                            checkIfFavorite(bookId);
                        }
                    }
                    isLoading.setValue(false);
                })
                .addOnFailureListener(e -> {
                    error.setValue("Failed to load book details");
                    isLoading.setValue(false);
                });
    }

    private void checkIfFavorite(String bookId) {
        isFavorite.setValue(favoritesManager.isFavorite(bookId));
    }

    public void toggleFavorite() {
        Book currentBook = book.getValue();
        if (currentBook != null) {
            if (isFavorite.getValue() != null && isFavorite.getValue()) {
                removeFromFavorites(currentBook.getId());
            } else {
                addToFavorites(currentBook.getId());
            }
        }
    }

    private void addToFavorites(String bookId) {
        favoritesManager.addToFavorites(bookId);
        isFavorite.setValue(true);
    }

    private void removeFromFavorites(String bookId) {
        favoritesManager.removeFromFavorites(bookId);
        isFavorite.setValue(false);
    }

    public void rentBook(int days) {
        Book currentBook = book.getValue();
        if (currentBook == null || !currentBook.isAvailable()) {
            error.setValue("Book is not available for rent");
            return;
        }

        isLoading.setValue(true);
        String userId = FirebaseUtils.getCurrentUserId();
        if (userId == null) {
            error.setValue("User not authenticated");
            isLoading.setValue(false);
            return;
        }

        Map<String, Object> rentalData = new HashMap<>();
        rentalData.put("bookId", currentBook.getId());
        rentalData.put("userId", userId);
        rentalData.put("rentalDate", System.currentTimeMillis());
        rentalData.put("returnDate", System.currentTimeMillis() + (days * 24 * 60 * 60 * 1000L));
        rentalData.put("status", "active");
        rentalData.put("totalAmount", currentBook.getDailyRate() * days);

        db.collection("rentals")
                .add(rentalData)
                .addOnSuccessListener(documentReference -> {
                    // Update book availability
                    db.collection("books").document(currentBook.getId())
                            .update("available", false)
                            .addOnSuccessListener(aVoid -> {
                                rentalSuccess.setValue(true);
                                isLoading.setValue(false);
                            })
                            .addOnFailureListener(e -> {
                                error.setValue("Failed to update book availability");
                                isLoading.setValue(false);
                            });
                })
                .addOnFailureListener(e -> {
                    error.setValue("Failed to create rental");
                    isLoading.setValue(false);
                });
    }
} 