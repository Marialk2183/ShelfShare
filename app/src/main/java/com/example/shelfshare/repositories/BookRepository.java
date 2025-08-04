package com.example.shelfshare.repositories;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import java.util.Map;
import java.util.HashMap;
import com.example.shelfshare.data.BookEntity;
import com.example.shelfshare.models.Book;

public class BookRepository {
    private final CollectionReference booksRef;
    private final MutableLiveData<List<BookEntity>> books = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final FirebaseFirestore db;
    private static final String COLLECTION_BOOKS = "books";

    public BookRepository() {
        this.booksRef = FirebaseFirestore.getInstance().collection("books");
        db = FirebaseFirestore.getInstance();
    }

    public LiveData<List<BookEntity>> getAllBooks() {
        isLoading.setValue(true);
        booksRef.get()
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
                    books.setValue(new ArrayList<>());
                    isLoading.setValue(false);
                });
        return books;
    }

    public LiveData<List<BookEntity>> getBooksByCategory(String categoryId) {
        isLoading.setValue(true);
        booksRef.whereEqualTo("categoryId", categoryId)
                .get()
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
                    books.setValue(new ArrayList<>());
                    isLoading.setValue(false);
                });
        return books;
    }

    public Query getAvailableBooks() {
        return booksRef.whereEqualTo("available", true);
    }

    public Query searchBooks(String query) {
        return booksRef.whereGreaterThanOrEqualTo("title", query)
                .whereLessThanOrEqualTo("title", query + "\uf8ff");
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public Task<DocumentSnapshot> getBookById(String bookId) {
        return booksRef.document(bookId).get();
    }

    public Task<Void> insertAll(List<BookEntity> books) {
        List<Task<Void>> tasks = new ArrayList<>();
        for (BookEntity book : books) {
            tasks.add(booksRef.document(book.getId()).set(book));
        }
        return Tasks.whenAll(tasks);
    }

    public Task<Void> insert(BookEntity book) {
        return booksRef.document(book.getId()).set(book);
    }

    public Task<Void> update(BookEntity book) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("title", book.getTitle());
        updates.put("author", book.getAuthor());
        updates.put("description", book.getDescription());
        updates.put("price", book.getPrice());
        updates.put("available", book.isAvailable());
        updates.put("categoryId", book.getCategoryId());
        updates.put("imageUrl", book.getImageUrl());
        updates.put("updatedAt", new Date());

        return booksRef.document(book.getId()).update(updates);
    }

    public Task<Void> delete(String bookId) {
        return booksRef.document(bookId).delete();
    }

    public Task<Void> updateAvailability(String bookId, boolean available) {
        return booksRef.document(bookId).update("available", available);
    }
//
//    public Query getBooksByCategory(String category) {
//        if (category.equals("All")) {
//            return db.collection(COLLECTION_BOOKS);
//        }
//        return db.collection(COLLECTION_BOOKS)
//                .whereEqualTo("category", category);
//    }

    public Query getBooksByLocationAndCategory(String locationId, String category) {
        Query query = db.collection(COLLECTION_BOOKS)
                .whereEqualTo("locationId", locationId);
        
        if (!category.equals("All")) {
            query = query.whereEqualTo("category", category);
        }
        
        return query;
    }

    public void getBooksByCategory(String category, OnBooksFetchedListener listener) {
        if (category.equals("All")) {
            db.collection(COLLECTION_BOOKS)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Book> books = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Book book = document.toObject(Book.class);
                        book.setId(document.getId());
                        books.add(book);
                    }
                    listener.onBooksFetched(books);
                })
                .addOnFailureListener(listener::onError);
        } else {
            db.collection(COLLECTION_BOOKS)
                .whereEqualTo("category", category)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Book> books = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Book book = document.toObject(Book.class);
                        book.setId(document.getId());
                        books.add(book);
                    }
                    listener.onBooksFetched(books);
                })
                .addOnFailureListener(listener::onError);
        }
    }

    public void getBooksByLocationAndCategory(String location, String category, OnBooksFetchedListener listener) {
        if (category.equals("All")) {
            db.collection(COLLECTION_BOOKS)
                .whereEqualTo("location", location)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Book> books = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Book book = document.toObject(Book.class);
                        book.setId(document.getId());
                        books.add(book);
                    }
                    listener.onBooksFetched(books);
                })
                .addOnFailureListener(listener::onError);
        } else {
            db.collection(COLLECTION_BOOKS)
                .whereEqualTo("location", location)
                .whereEqualTo("category", category)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Book> books = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Book book = document.toObject(Book.class);
                        book.setId(document.getId());
                        books.add(book);
                    }
                    listener.onBooksFetched(books);
                })
                .addOnFailureListener(listener::onError);
        }
    }

    public interface OnBooksFetchedListener {
        void onBooksFetched(List<Book> books);
        void onError(Exception e);
    }
} 