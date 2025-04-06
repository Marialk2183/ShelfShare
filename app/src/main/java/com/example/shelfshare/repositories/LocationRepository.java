package com.example.shelfshare.repositories;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.example.shelfshare.data.Location;
import java.util.ArrayList;
import java.util.List;
import androidx.lifecycle.MutableLiveData;

public class LocationRepository {
    private final FirebaseFirestore db;
    private final MutableLiveData<List<Location>> locations = new MutableLiveData<>();

    public LocationRepository() {
        this.db = FirebaseFirestore.getInstance();
    }

    public Query getAllLocations() {
        return db.collection("locations")
                .orderBy("name");
    }

    public Task<DocumentReference> addLocation(Location location) {
        return db.collection("locations").add(location);
    }

    public Task<Void> updateLocation(String locationId, Location location) {
        return db.collection("locations").document(locationId).set(location);
    }

    public Task<Void> deleteLocation(String locationId) {
        return db.collection("locations").document(locationId).delete();
    }
} 