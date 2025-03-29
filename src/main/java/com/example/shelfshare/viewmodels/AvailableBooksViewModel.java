package com.example.shelfshare.viewmodels;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.example.shelfshare.data.BookEntity;
import com.example.shelfshare.data.BookRepository;
import java.util.List;

public class AvailableBooksViewModel extends AndroidViewModel {
    private BookRepository repository;
    private LiveData<List<BookEntity>> availableBooks;

    public AvailableBooksViewModel(Application application) {
        super(application);
        repository = new BookRepository(application);
        availableBooks = repository.getAvailableBooks();
    }

    public LiveData<List<BookEntity>> getAvailableBooks() {
        return availableBooks;
    }

    public void insert(BookEntity book) {
        repository.insert(book);
    }

    public void insertAll(List<BookEntity> books) {
        repository.insertAll(books);
    }

    public void update(BookEntity book) {
        repository.update(book);
    }

    public void delete(BookEntity book) {
        repository.delete(book);
    }

    public void updateFavoriteStatus(String bookId, boolean isFavorite) {
        repository.updateFavoriteStatus(bookId, isFavorite);
    }

    public LiveData<List<BookEntity>> searchBooks(String query) {
        return repository.searchBooks(query);
    }
} 