package com.example.shelfshare.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.shelfshare.data.BookEntity;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class MyBooksViewModel extends ViewModel {
    private final MutableLiveData<List<BookEntity>> myBooks = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> error = new MutableLiveData<>(null);

    public MyBooksViewModel() {
        loadMyBooks();  // Load books when ViewModel is initialized
    }

    public LiveData<List<BookEntity>> getMyBooks() {
        return myBooks;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getError() {
        return error;
    }

    private void loadMyBooks() {
        isLoading.setValue(true);

        // Simulate fetching user's books from Firestore
        FirebaseFirestore.getInstance()
                .collection("myBooks") // this collection can be user-specific
                .get()
                .addOnCompleteListener(task -> {
                    isLoading.setValue(false);
                    if (task.isSuccessful() && task.getResult() != null) {
                        List<BookEntity> list = new ArrayList<>();
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            BookEntity book = new BookEntity();
                            book.setId(doc.getId());
                            book.setTitle(doc.getString("title"));
                            book.setAuthor(doc.getString("author"));
                            book.setImageUrl(doc.getString("imageUrl"));
                            book.setPrice(doc.getDouble("price") != null ? doc.getDouble("price") : 0.0);
                            list.add(book);
                        }
                        myBooks.setValue(list);
                    } else {
                        error.setValue(task.getException() != null ? task.getException().getMessage() : "Failed to load books");
                    }
                });
    }
}
