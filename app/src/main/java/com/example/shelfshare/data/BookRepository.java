package com.example.shelfshare.data;

import android.app.Application;
import androidx.lifecycle.LiveData;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BookRepository {
    private BookDao bookDao;
    private ExecutorService executorService;

    public BookRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        bookDao = db.bookDao();
        executorService = Executors.newSingleThreadExecutor();
    }

    public LiveData<List<BookEntity>> getAllBooks() {
        return bookDao.getAllBooks();
    }

    public LiveData<List<BookEntity>> getAvailableBooks() {
        return bookDao.getAvailableBooks();
    }

    public LiveData<List<BookEntity>> getFavoriteBooks() {
        return bookDao.getFavoriteBooks();
    }

    public LiveData<List<BookEntity>> searchBooks(String query) {
        return bookDao.searchBooks(query);
    }

    public void insert(BookEntity book) {
        executorService.execute(() -> bookDao.insert(book));
    }

    public void insertAll(List<BookEntity> books) {
        executorService.execute(() -> bookDao.insertAll(books));
    }

    public void update(BookEntity book) {
        executorService.execute(() -> bookDao.update(book));
    }

    public void delete(BookEntity book) {
        executorService.execute(() -> bookDao.delete(book));
    }

    public void updateFavoriteStatus(String bookId, boolean isFavorite) {
        executorService.execute(() -> bookDao.updateFavoriteStatus(bookId, isFavorite));
    }
} 