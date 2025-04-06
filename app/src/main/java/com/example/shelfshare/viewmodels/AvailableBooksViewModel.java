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
    private final MutableLiveData<List<BookEntity>> books = new MutableLiveData<>();
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
                books.setValue(bookList);
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
                books.setValue(bookList);
                isLoading.setValue(false);
            })
            .addOnFailureListener(e -> {
                error.setValue(e.getMessage());
                isLoading.setValue(false);
            });
    }

    public LiveData<List<BookEntity>> getBooks() {
        return books;
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
        return repository.insertAll(books);
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
} 