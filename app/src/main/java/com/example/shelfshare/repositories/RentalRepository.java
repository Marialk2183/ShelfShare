package com.example.shelfshare.repositories;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.shelfshare.data.Rental;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Query;
import com.example.shelfshare.data.BookEntity;
import com.google.firebase.firestore.CollectionReference;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RentalRepository {
    private final CollectionReference rentalsRef;
    private final String userId;
    private final MutableLiveData<List<Rental>> rentals = new MutableLiveData<>();

    public RentalRepository(String userId) {
        this.userId = userId;
        this.rentalsRef = FirebaseFirestore.getInstance().collection("rentals");
    }

    public LiveData<List<Rental>> getAllRentals() {
        rentalsRef.get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                List<Rental> rentalList = new ArrayList<>();
                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    Rental rental = document.toObject(Rental.class);
                    rental.setId(document.getId());
                    rentalList.add(rental);
                }
                rentals.setValue(rentalList);
            });
        return rentals;
    }

    public Query getRentals() {
        return rentalsRef.whereEqualTo("userId", userId);
    }

    public Task<Void> createRental(BookEntity book, String userId, int duration) {
        Rental rental = new Rental();
        rental.setBookId(book.getId());
        rental.setUserId(userId);
        rental.setBookTitle(book.getTitle());
        rental.setPrice(book.getPrice());
        rental.setDuration(duration);
        rental.setStartDate(new Date());
        rental.setEndDate(new Date(System.currentTimeMillis() + duration * 24 * 60 * 60 * 1000L));
        rental.setStatus("pending");
        rental.setTotalPrice(book.getPrice() * duration);

        return rentalsRef.add(rental)
                .continueWith(task -> {
                    if (task.isSuccessful()) {
                        DocumentReference docRef = task.getResult();
                        rental.setId(docRef.getId());
                        return null;
                    } else {
                        throw task.getException();
                    }
                });
    }

    public Task<Void> updateRentalStatus(String rentalId, String status) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("status", status);
        return rentalsRef.document(rentalId).update(updates);
    }

    public Task<Void> returnRental(String rentalId) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("status", "returned");
        updates.put("returnDate", new Date());
        return rentalsRef.document(rentalId).update(updates);
    }

    public Task<Void> rentBook(BookEntity book, int days, double price) {
        Rental rental = new Rental();
        rental.setBookId(book.getId());
        rental.setUserId(userId);
        rental.setStartDate(new Date());
        rental.setEndDate(new Date(System.currentTimeMillis() + (days * 24 * 60 * 60 * 1000)));
        rental.setPrice(price);
        rental.setStatus("pending");
        
        return rentalsRef
            .add(rental)
            .continueWithTask(task -> {
                if (task.isSuccessful()) {
                    return rentalsRef
                        .document(task.getResult().getId())
                        .update("status", "rented");
                }
                return Tasks.forException(task.getException());
            });
    }
} 