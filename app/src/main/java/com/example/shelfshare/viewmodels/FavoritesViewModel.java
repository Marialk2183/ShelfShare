package com.example.shelfshare.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.shelfshare.data.BookEntity;
import com.example.shelfshare.repositories.BookRepository;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QuerySnapshot;

public class FavoritesViewModel extends ViewModel {
    private final BookRepository bookRepository;
    private final MutableLiveData<List<BookEntity>> favorites = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> error = new MutableLiveData<>();

    public FavoritesViewModel() {
        bookRepository = new BookRepository();
    }

    public void loadFavorites() {
        isLoading.setValue(true);
        bookRepository.getAllBooks()
                .observeForever(bookList -> {
                    List<BookEntity> favoriteList = new ArrayList<>();
                    for (BookEntity book : bookList) {
                        if (book.isFavorite()) {
                            favoriteList.add(book);
                        }
                    }
                    favorites.setValue(favoriteList);
                    isLoading.setValue(false);
                });
    }

    public Task<Void> addToFavorites(BookEntity book) {
        isLoading.setValue(true);
        return bookRepository.update(book)
                .addOnSuccessListener(aVoid -> {
                    List<BookEntity> currentFavorites = favorites.getValue();
                    if (currentFavorites == null) {
                        currentFavorites = new ArrayList<>();
                    }
                    currentFavorites.add(book);
                    favorites.setValue(currentFavorites);
                    isLoading.setValue(false);
                })
                .addOnFailureListener(e -> {
                    error.setValue(e.getMessage());
                    isLoading.setValue(false);
                });
    }

    public Task<Void> removeFromFavorites(BookEntity book) {
        isLoading.setValue(true);
        book.setFavorite(false);
        return bookRepository.update(book)
                .addOnSuccessListener(aVoid -> {
                    List<BookEntity> currentFavorites = favorites.getValue();
                    if (currentFavorites != null) {
                        currentFavorites.remove(book);
                        favorites.setValue(currentFavorites);
                    }
                    isLoading.setValue(false);
                })
                .addOnFailureListener(e -> {
                    error.setValue(e.getMessage());
                    isLoading.setValue(false);
                });
    }

    public LiveData<List<BookEntity>> getFavorites() {
        return favorites;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getError() {
        return error;
    }
} 