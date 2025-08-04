package com.example.shelfshare.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.shelfshare.data.Rental;
import com.example.shelfshare.repositories.RentalRepository;
import com.example.shelfshare.utils.FirebaseUtils;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;
import java.util.ArrayList;

public class ReturnListViewModel extends ViewModel {
    private final RentalRepository repository;
    private final MutableLiveData<List<Rental>> rentals = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> error = new MutableLiveData<>();

    public ReturnListViewModel() {
        repository = new RentalRepository(FirebaseUtils.getCurrentUserId());
    }

    public void loadRentals() {
        isLoading.setValue(true);
        repository.getAllRentals().observeForever(rentalsList -> {
            if (rentalsList != null) {
                rentals.setValue(rentalsList);
                isLoading.setValue(false);
            }
        });
    }

    public Task<Void> confirmReturn(Rental rental) {
        isLoading.setValue(true);
        return repository.updateRentalStatus(rental.getId(), "returned")
            .addOnSuccessListener(aVoid -> {
                loadRentals();
                isLoading.setValue(false);
            })
            .addOnFailureListener(e -> {
                error.setValue(e.getMessage());
                isLoading.setValue(false);
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
} 