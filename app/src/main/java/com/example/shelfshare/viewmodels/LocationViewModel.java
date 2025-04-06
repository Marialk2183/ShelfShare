package com.example.shelfshare.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.shelfshare.models.Location;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class LocationViewModel extends ViewModel {
    private final MutableLiveData<List<Location>> locations = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final FirebaseFirestore db;

    public LocationViewModel() {
        db = FirebaseFirestore.getInstance();
    }

    public LiveData<List<Location>> getLocations() {
        return locations;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getError() {
        return error;
    }

    public void loadLocations() {
        isLoading.setValue(true);
        db.collection("locations")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Location> locationList = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Location location = document.toObject(Location.class);
                        location.setId(document.getId());
                        locationList.add(location);
                    }
                    locations.setValue(locationList);
                    isLoading.setValue(false);
                })
                .addOnFailureListener(e -> {
                    error.setValue("Failed to load locations");
                    isLoading.setValue(false);
                });
    }
} 