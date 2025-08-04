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
    private final MutableLiveData<List<Location>> locations = new MutableLiveData<>();
    private final MutableLiveData<Location> currentLocation = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private final FirebaseFirestore db;

    public LocationViewModel() {
        db = FirebaseFirestore.getInstance();
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
                // Handle error
                isLoading.setValue(false);
            });
    }

    public void selectLocation(Location location) {
        currentLocation.setValue(location);
        // Save selected location to SharedPreferences or Firebase
        saveSelectedLocation(location);
    }

    private void saveSelectedLocation(Location location) {
        // Save to SharedPreferences or Firebase user data
        // This is just a placeholder - implement actual storage logic
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
} 