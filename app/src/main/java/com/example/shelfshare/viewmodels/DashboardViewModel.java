package com.example.shelfshare.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.shelfshare.data.BookEntity;
import com.example.shelfshare.databinding.ActivityDashboardBinding;
import com.example.shelfshare.models.Book;
import com.example.shelfshare.models.Category;
import com.example.shelfshare.models.Location;
import com.example.shelfshare.repositories.BookRepository;
import com.example.shelfshare.utils.FirebaseUtils;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class DashboardViewModel extends ViewModel {
    private final FirebaseUtils firebaseUtils;
    private final BookRepository bookRepository;
    private final MutableLiveData<List<Category>> categories = new MutableLiveData<>();
    private final MutableLiveData<List<BookEntity>> books = new MutableLiveData<>();
    private final MutableLiveData<String> currentLocation = new MutableLiveData<>();
    private final MutableLiveData<FirebaseUser> currentUser = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<Location> selectedLocation = new MutableLiveData<>();
    private ActivityDashboardBinding contentBinding;

    public DashboardViewModel() {
        firebaseUtils = FirebaseUtils.getInstance();
        bookRepository = new BookRepository();
        loadCategories();
        loadUserData();
    }

    public LiveData<List<Category>> getCategories() {
        return categories;
    }

    public LiveData<List<BookEntity>> getBooks() {
        return books;
    }

    public LiveData<String> getCurrentLocation() {
        return currentLocation;
    }

    public LiveData<FirebaseUser> getCurrentUser() {
        return currentUser;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<Location> getSelectedLocation() {
        return selectedLocation;
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
                    List<BookEntity> bookList = new ArrayList<>();
                    for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                        BookEntity book = document.toObject(BookEntity.class);
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

    public void loadBooksByCategory(String category) {
        isLoading.setValue(true);
        bookRepository.getBooksByCategory(category, new BookRepository.OnBooksFetchedListener() {
            @Override
            public void onBooksFetched(List<Book> bookList) {
                List<BookEntity> bookEntityList = new ArrayList<>();
                for (Book book : bookList) {
                    BookEntity bookEntity = new BookEntity();
                    bookEntity.setId(book.getId());
                    bookEntity.setTitle(book.getTitle());
                    bookEntity.setAuthor(book.getAuthor());
                    bookEntity.setDescription(book.getDescription());
                    bookEntity.setPrice(book.getPrice());
                    bookEntity.setImageUrl(book.getImageUrl());
                    bookEntity.setCategory(book.getCategory());
                    bookEntity.setLocation(book.getLocation());
                    bookEntity.setAvailable(book.isAvailable());
                    bookEntityList.add(bookEntity);
                }
                books.setValue(bookEntityList);
                isLoading.setValue(false);
            }

            @Override
            public void onError(Exception e) {
                books.setValue(new ArrayList<>());
                isLoading.setValue(false);
            }
        });
    }

    public void loadBooksByLocationAndCategory(String locationId, String category) {
        isLoading.setValue(true);
        bookRepository.getBooksByLocationAndCategory(locationId, category, new BookRepository.OnBooksFetchedListener() {
            @Override
            public void onBooksFetched(List<Book> bookList) {
                List<BookEntity> bookEntityList = new ArrayList<>();
                for (Book book : bookList) {
                    BookEntity bookEntity = new BookEntity();
                    bookEntity.setId(book.getId());
                    bookEntity.setTitle(book.getTitle());
                    bookEntity.setAuthor(book.getAuthor());
                    bookEntity.setDescription(book.getDescription());
                    bookEntity.setPrice(book.getPrice());
                    bookEntity.setImageUrl(book.getImageUrl());
                    bookEntity.setCategory(book.getCategory());
                    bookEntity.setLocation(book.getLocation());
                    bookEntity.setAvailable(book.isAvailable());
                    bookEntityList.add(bookEntity);
                }
                books.setValue(bookEntityList);
                isLoading.setValue(false);
            }

            @Override
            public void onError(Exception e) {
                books.setValue(new ArrayList<>());
                isLoading.setValue(false);
            }
        });
    }

    public void setSelectedLocation(Location location) {
        selectedLocation.setValue(location);
    }
}