package com.example.shelfshare.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.shelfshare.data.BookEntity;
import com.example.shelfshare.data.Rental;
import com.example.shelfshare.repositories.BookRepository;
import com.example.shelfshare.repositories.RentalRepository;
import com.example.shelfshare.utils.FirebaseUtils;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class RentalViewModel extends ViewModel {
    private final RentalRepository repository;
    private final MutableLiveData<List<Rental>> rentals = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<RentalState> rentalState = new MutableLiveData<>();

    public enum RentalState {
        IDLE, LOADING, SUCCESS, ERROR
    }

    public RentalViewModel() {
        repository = new RentalRepository(FirebaseUtils.getCurrentUserId());
    }

    public void loadRentals() {
        isLoading.setValue(true);
        repository.getRentals().get()
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
                    error.setValue(e.getMessage());
                    isLoading.setValue(false);
                });
    }

    public void confirmRental(Rental rental) {
        isLoading.setValue(true);
        repository.updateRentalStatus(rental.getId(), "confirmed")
                .addOnSuccessListener(aVoid -> {
                    loadRentals();
                    isLoading.setValue(false);
                })
                .addOnFailureListener(e -> {
                    error.setValue(e.getMessage());
                    isLoading.setValue(false);
                });
    }

    public void createRental(BookEntity book, int days, double price) {
        isLoading.setValue(true);
        repository.rentBook(book, days, price)
            .addOnSuccessListener(aVoid -> {
                isLoading.setValue(false);
                rentalState.setValue(RentalState.SUCCESS);
            })
            .addOnFailureListener(e -> {
                isLoading.setValue(false);
                error.setValue(e.getMessage());
                rentalState.setValue(RentalState.ERROR);
            });
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

    public LiveData<RentalState> getRentalState() {
        return rentalState;
    }
} 