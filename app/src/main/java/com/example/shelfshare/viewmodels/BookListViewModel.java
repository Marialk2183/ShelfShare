package com.example.shelfshare.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.shelfshare.data.BookEntity;
import com.example.shelfshare.repositories.BookRepository;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class BookListViewModel extends ViewModel {
    private final BookRepository repository;
    private final MutableLiveData<List<BookEntity>> books = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> error = new MutableLiveData<>();

    public BookListViewModel() {
        repository = new BookRepository();
    }

    public void loadBooks() {
        isLoading.setValue(true);
        repository.getAvailableBooks().get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<BookEntity> bookList = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        BookEntity book = document.toObject(BookEntity.class);
                        book.setId(document.getId());
                        bookList.add(book);
                    }
                    books.setValue(bookList);
                    isLoading.setValue(false);
                })
                .addOnFailureListener(e -> {
                    error.setValue(e.getMessage());
                    isLoading.setValue(false);
                });
    }

    public void loadBooksByCategory(String categoryId) {
        isLoading.setValue(true);
        repository.getBooksByCategory(categoryId).observeForever(bookList -> {
            books.setValue(bookList);
            isLoading.setValue(false);
        });
    }

    public void searchBooks(String query) {
        isLoading.setValue(true);
        repository.searchBooks(query).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<BookEntity> bookList = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        BookEntity book = document.toObject(BookEntity.class);
                        book.setId(document.getId());
                        bookList.add(book);
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
}