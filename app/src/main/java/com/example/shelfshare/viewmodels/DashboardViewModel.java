package com.example.shelfshare.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.shelfshare.models.Book;
import com.example.shelfshare.models.Category;
import com.example.shelfshare.utils.FirebaseUtils;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class DashboardViewModel extends ViewModel {
    private final FirebaseUtils firebaseUtils;
    private final MutableLiveData<List<Category>> categories = new MutableLiveData<>();
    private final MutableLiveData<List<Book>> books = new MutableLiveData<>();
    private final MutableLiveData<String> currentLocation = new MutableLiveData<>();
    private final MutableLiveData<FirebaseUser> currentUser = new MutableLiveData<>();

    public DashboardViewModel() {
        firebaseUtils = FirebaseUtils.getInstance();
        loadCategories();
        loadUserData();
    }

    public LiveData<List<Category>> getCategories() {
        return categories;
    }

    public LiveData<List<Book>> getBooks() {
        return books;
    }

    public LiveData<String> getCurrentLocation() {
        return currentLocation;
    }

    public LiveData<FirebaseUser> getCurrentUser() {
        return currentUser;
    }

    private void loadCategories() {
        // Load categories from Firestore
        firebaseUtils.getFirestore()
                .collection("categories")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Category> categoryList = new ArrayList<>();
                    for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                        Category category = document.toObject(Category.class);
                        if (category != null) {
                            category.setId(document.getId());
                            categoryList.add(category);
                        }
                    }
                    categories.postValue(categoryList);
                });
    }

    public void loadBooksByLocation(String location) {
        // Load books by location from Firestore
        firebaseUtils.getFirestore()
                .collection("books")
                .whereEqualTo("location", location)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Book> bookList = new ArrayList<>();
                    for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                        Book book = document.toObject(Book.class);
                        if (book != null) {
                            book.setId(document.getId());
                            bookList.add(book);
                        }
                    }
                    books.postValue(bookList);
                });
    }

    private void loadUserData() {
        FirebaseUser user = firebaseUtils.getCurrentUser();
        if (user != null) {
            currentUser.setValue(user);
            // Load user's location preference
            firebaseUtils.getFirestore()
                    .collection("users")
                    .document(user.getUid())
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String location = documentSnapshot.getString("location");
                            if (location != null && !location.isEmpty()) {
                                currentLocation.setValue(location);
                                loadBooksByLocation(location);
                            }
                        }
                    });
        }
    }

    public void updateLocation(String location) {
        FirebaseUser user = firebaseUtils.getCurrentUser();
        if (user != null) {
            firebaseUtils.getFirestore()
                    .collection("users")
                    .document(user.getUid())
                    .update("location", location)
                    .addOnSuccessListener(aVoid -> {
                        currentLocation.setValue(location);
                        loadBooksByLocation(location);
                    });
        }
    }

    public void signOut() {
        firebaseUtils.signOut();
        currentUser.setValue(null);
    }
} 