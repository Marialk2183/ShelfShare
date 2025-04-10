package com.example.shelfshare.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.shelfshare.data.BookEntity;
import com.example.shelfshare.repositories.BookRepository;
import com.example.shelfshare.repositories.RentalRepository;
import com.example.shelfshare.utils.FirebaseUtils;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

public class BookDetailsViewModel extends ViewModel {
    private final BookRepository bookRepository;
    private final RentalRepository rentalRepository;
    private final MutableLiveData<BookEntity> book = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isFavorite = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<Boolean> rentSuccess = new MutableLiveData<>();

    public BookDetailsViewModel() {
        bookRepository = new BookRepository();
        rentalRepository = new RentalRepository(FirebaseUtils.getCurrentUserId());
    }

    public void loadBookDetails(String bookId) {
        isLoading.setValue(true);
        bookRepository.getBookById(bookId)
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        BookEntity bookEntity = documentSnapshot.toObject(BookEntity.class);
                        if (bookEntity != null) {
                            bookEntity.setId(documentSnapshot.getId());
                            book.setValue(bookEntity);
                            checkFavoriteStatus(bookId);
                        }
                    } else {
                        error.setValue("Book not found");
                    }
                    isLoading.setValue(false);
                })
                .addOnFailureListener(e -> {
                    error.setValue(e.getMessage());
                    isLoading.setValue(false);
                });
    }

    private void checkFavoriteStatus(String bookId) {
        // Implement favorite status check logic here
        // This is a placeholder - you'll need to implement the actual favorite status check
        isFavorite.setValue(false);
    }

    public void addToFavorites(String bookId) {
        // Implement add to favorites logic here
        // This is a placeholder - you'll need to implement the actual favorite addition
        isFavorite.setValue(true);
    }

    public void removeFromFavorites(String bookId) {
        // Implement remove from favorites logic here
        // This is a placeholder - you'll need to implement the actual favorite removal
        isFavorite.setValue(false);
    }

    public void createRental(String bookId, String userId, int duration) {
        isLoading.setValue(true);
        bookRepository.getBookById(bookId)
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        BookEntity bookEntity = documentSnapshot.toObject(BookEntity.class);
                        if (bookEntity != null) {
                            rentalRepository.createRental(bookEntity, userId, duration)
                                    .addOnSuccessListener(aVoid -> {
                                        rentSuccess.setValue(true);
                                        isLoading.setValue(false);
                                    })
                                    .addOnFailureListener(e -> {
                                        error.setValue(e.getMessage());
                                        isLoading.setValue(false);
                                    });
                        }
                    } else {
                        error.setValue("Book not found");
                        isLoading.setValue(false);
                    }
                })
                .addOnFailureListener(e -> {
                    error.setValue(e.getMessage());
                    isLoading.setValue(false);
                });
    }

    public void rentBook(BookEntity book, int days, double price) {
        isLoading.setValue(true);
        rentalRepository.rentBook(book, days, price)
            .addOnSuccessListener(aVoid -> {
                isLoading.setValue(false);
                rentSuccess.setValue(true);
            })
            .addOnFailureListener(e -> {
                isLoading.setValue(false);
                error.setValue(e.getMessage());
                rentSuccess.setValue(false);
            });
    }

    public LiveData<BookEntity> getBook() {
        return book;
    }

    public LiveData<Boolean> getIsFavorite() {
        return isFavorite;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getError() {
        return error;
    }

    public LiveData<Boolean> getRentSuccess() {
        return rentSuccess;
    }
} 