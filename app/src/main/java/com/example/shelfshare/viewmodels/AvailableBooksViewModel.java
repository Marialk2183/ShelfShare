package com.example.shelfshare.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.shelfshare.repositories.BookRepository;
import com.example.shelfshare.data.BookEntity;
import java.util.List;
import java.util.ArrayList;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QuerySnapshot;

public class AvailableBooksViewModel extends ViewModel {
    private final BookRepository repository;
    private final MutableLiveData<List<BookEntity>> availableBooks = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<List<BookEntity>> favoriteBooks = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> error = new MutableLiveData<>();

    public AvailableBooksViewModel() {
        repository = new BookRepository();
    }

    public void loadBooks() {
        isLoading.setValue(true);
        repository.getAvailableBooks()
            .get()
            .addOnSuccessListener(querySnapshot -> {
                List<BookEntity> bookList = new ArrayList<>();
                for (var document : querySnapshot.getDocuments()) {
                    BookEntity book = document.toObject(BookEntity.class);
                    if (book != null) {
                        book.setId(document.getId());
                        bookList.add(book);
                    }
                }
                availableBooks.setValue(bookList);
                isLoading.setValue(false);
            })
            .addOnFailureListener(e -> {
                error.setValue(e.getMessage());
                isLoading.setValue(false);
            });
    }

    public void searchBooks(String query) {
        isLoading.setValue(true);
        repository.searchBooks(query)
            .get()
            .addOnSuccessListener(querySnapshot -> {
                List<BookEntity> bookList = new ArrayList<>();
                for (var document : querySnapshot.getDocuments()) {
                    BookEntity book = document.toObject(BookEntity.class);
                    if (book != null && book.isAvailable()) {
                        book.setId(document.getId());
                        bookList.add(book);
                    }
                }
                availableBooks.setValue(bookList);
                isLoading.setValue(false);
            })
            .addOnFailureListener(e -> {
                error.setValue(e.getMessage());
                isLoading.setValue(false);
            });
    }

    public LiveData<List<BookEntity>> getAvailableBooks() {
        return availableBooks;
    }

    public LiveData<List<BookEntity>> getFavoriteBooks() {
        return favoriteBooks;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getError() {
        return error;
    }

    public Task<Void> insert(BookEntity book) {
        return repository.insert(book);
    }

    public Task<Void> insertAll(List<BookEntity> books) {
        // Add books to the repository
        Task<Void> task = repository.insertAll(books);
        
        // Also update the local LiveData
        List<BookEntity> currentBooks = availableBooks.getValue();
        if (currentBooks != null) {
            currentBooks.addAll(books);
            availableBooks.setValue(currentBooks);
        }
        
        return task;
    }

    public Task<Void> update(BookEntity book) {
        return repository.update(book);
    }

    public Task<Void> delete(String bookId) {
        return repository.delete(bookId);
    }

    public Task<Void> updateAvailability(String bookId, boolean available) {
        return repository.updateAvailability(bookId, available);
    }

    public void toggleFavorite(BookEntity book) {
        book.setFavorite(!book.isFavorite());
        
        List<BookEntity> currentFavorites = favoriteBooks.getValue();
        if (currentFavorites != null) {
            if (book.isFavorite()) {
                if (!currentFavorites.contains(book)) {
                    currentFavorites.add(book);
                }
            } else {
                currentFavorites.remove(book);
            }
            favoriteBooks.setValue(currentFavorites);
        }
        
        // Update the book in the available books list
        List<BookEntity> currentBooks = availableBooks.getValue();
        if (currentBooks != null) {
            int index = currentBooks.indexOf(book);
            if (index != -1) {
                currentBooks.set(index, book);
                availableBooks.setValue(currentBooks);
            }
        }
    }
} 