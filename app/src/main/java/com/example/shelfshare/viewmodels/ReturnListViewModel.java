package com.example.shelfshare.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.shelfshare.models.Rental;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ReturnListViewModel extends ViewModel {
    private final MutableLiveData<List<Rental>> rentals = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final FirebaseFirestore db;

    public ReturnListViewModel() {
        db = FirebaseFirestore.getInstance();
    }

    public LiveData<List<Rental>> getRentals() {
        return rentals;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getError() {
        return error;
    }

    public void loadRentals(String userId) {
        isLoading.setValue(true);
        db.collection("rentals")
                .whereEqualTo("userId", userId)
                .whereEqualTo("status", "active")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Rental> rentalList = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Rental rental = document.toObject(Rental.class);
                        rental.setId(document.getId());
                        rentalList.add(rental);
                    }
                    rentals.setValue(rentalList);
                    isLoading.setValue(false);
                })
                .addOnFailureListener(e -> {
                    error.setValue("Failed to load rentals");
                    isLoading.setValue(false);
                });
    }

    public void returnBook(String rentalId) {
        isLoading.setValue(true);
        db.collection("rentals")
                .document(rentalId)
                .update("status", "returned")
                .addOnSuccessListener(aVoid -> {
                    List<Rental> currentRentals = rentals.getValue();
                    if (currentRentals != null) {
                        for (Rental rental : currentRentals) {
                            if (rental.getId().equals(rentalId)) {
                                rental.setStatus("returned");
                                break;
                            }
                        }
                        rentals.setValue(currentRentals);
                    }
                    isLoading.setValue(false);
                })
                .addOnFailureListener(e -> {
                    error.setValue("Failed to return book");
                    isLoading.setValue(false);
                });
    }
} 