package com.example.shelfshare.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.shelfshare.data.Location;
import com.example.shelfshare.repositories.LocationRepository;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;
import java.util.ArrayList;

public class LocationViewModel extends ViewModel {
    private final LocationRepository repository;
    private final MutableLiveData<List<Location>> locations = new MutableLiveData<>();
    private final MutableLiveData<Location> currentLocation = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> error = new MutableLiveData<>();

    public LocationViewModel() {
        repository = new LocationRepository();
    }

    public void loadLocations() {
        isLoading.setValue(true);
        repository.getAllLocations()
            .get()
            .addOnSuccessListener(querySnapshot -> {
                List<Location> locationList = new ArrayList<>();
                for (var document : querySnapshot.getDocuments()) {
                    Location location = document.toObject(Location.class);
                    if (location != null) {
                        location.setId(document.getId());
                        locationList.add(location);
                    }
                }
                locations.setValue(locationList);
                isLoading.setValue(false);
            })
            .addOnFailureListener(e -> {
                error.setValue(e.getMessage());
                isLoading.setValue(false);
            });
    }

    public Task<DocumentReference> addLocation(Location location) {
        return repository.addLocation(location);
    }

    public Task<Void> updateLocation(String locationId, Location location) {
        return repository.updateLocation(locationId, location);
    }

    public Task<Void> deleteLocation(String locationId) {
        return repository.deleteLocation(locationId);
    }

    public void setCurrentLocation(Location location) {
        currentLocation.setValue(location);
    }

    public void selectLocation(Location location) {
        currentLocation.setValue(location);
    }

    public LiveData<List<Location>> getLocations() {
        return locations;
    }

    public LiveData<Location> getCurrentLocation() {
        return currentLocation;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getError() {
        return error;
    }
} 