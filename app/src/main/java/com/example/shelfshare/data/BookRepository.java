package com.example.shelfshare.data;

import android.app.Application;
import androidx.lifecycle.LiveData;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import android.util.Log;

import com.example.shelfshare.models.Book;
import com.example.shelfshare.utils.FirebaseUtils;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

public class BookRepository {
    private static final String TAG = "BookRepository";
    private static BookRepository instance;
    private final FirebaseUtils firebaseUtils;
    private BookDao bookDao;
    private ExecutorService executorService;

    private BookRepository() {
        firebaseUtils = FirebaseUtils.getInstance();
    }

    public static synchronized BookRepository getInstance() {
        if (instance == null) {
            instance = new BookRepository();
        }
        return instance;
    }

    public void init(Application application) {
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

    public CompletableFuture<List<Book>> getBooksByLocation(String location) {
        CompletableFuture<List<Book>> future = new CompletableFuture<>();
        
        firebaseUtils.getFirestore()
                .collection("books")
                .whereEqualTo("location", location)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Book> books = new ArrayList<>();
                    for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                        Book book = document.toObject(Book.class);
                        if (book != null) {
                            book.setId(document.getId());
                            books.add(book);
                        }
                    }
                    future.complete(books);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error getting books by location", e);
                    future.completeExceptionally(e);
                });
        
        return future;
    }

    public CompletableFuture<List<Book>> getBooksByCategory(String category, String location) {
        CompletableFuture<List<Book>> future = new CompletableFuture<>();
        
        firebaseUtils.getFirestore()
                .collection("books")
                .whereEqualTo("category", category)
                .whereEqualTo("location", location)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Book> books = new ArrayList<>();
                    for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                        Book book = document.toObject(Book.class);
                        if (book != null) {
                            book.setId(document.getId());
                            books.add(book);
                        }
                    }
                    future.complete(books);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error getting books by category", e);
                    future.completeExceptionally(e);
                });
        
        return future;
    }

    public CompletableFuture<Boolean> addBook(Book book) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        
        firebaseUtils.getFirestore()
                .collection("books")
                .add(book)
                .addOnSuccessListener(documentReference -> {
                    book.setId(documentReference.getId());
                    future.complete(true);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error adding book", e);
                    future.complete(false);
                });
        
        return future;
    }

    public CompletableFuture<Boolean> updateBook(Book book) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        
        if (book.getId() == null) {
            future.complete(false);
            return future;
        }
        
        firebaseUtils.getFirestore()
                .collection("books")
                .document(book.getId())
                .set(book)
                .addOnSuccessListener(aVoid -> future.complete(true))
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error updating book", e);
                    future.complete(false);
                });
        
        return future;
    }

    public CompletableFuture<Boolean> deleteBook(String bookId) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        
        firebaseUtils.getFirestore()
                .collection("books")
                .document(bookId)
                .delete()
                .addOnSuccessListener(aVoid -> future.complete(true))
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error deleting book", e);
                    future.complete(false);
                });
        
        return future;
    }
} 