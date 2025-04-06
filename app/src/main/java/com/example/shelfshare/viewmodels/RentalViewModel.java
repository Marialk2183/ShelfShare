package com.example.shelfshare.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.shelfshare.models.Rental;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class RentalViewModel extends ViewModel {
    private final MutableLiveData<List<Rental>> rentals = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final FirebaseFirestore db;

    public RentalViewModel() {
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

    public void createRental(Rental rental) {
        isLoading.setValue(true);
        db.collection("rentals")
                .add(rental)
                .addOnSuccessListener(documentReference -> {
                    rental.setId(documentReference.getId());
                    List<Rental> currentRentals = rentals.getValue();
                    if (currentRentals != null) {
                        currentRentals.add(rental);
                        rentals.setValue(currentRentals);
                    }
                    isLoading.setValue(false);
                })
                .addOnFailureListener(e -> {
                    error.setValue("Failed to create rental");
                    isLoading.setValue(false);
                });
    }
} 