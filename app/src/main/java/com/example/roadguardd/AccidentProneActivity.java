package com.example.roadguardd;

import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class AccidentProneActivity extends AppCompatActivity {

    ListView listView;
    AccidentProneAdapter adapter;
    ArrayList<AccidentLocation> locationList = new ArrayList<>();
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accident_prone);

        listView = findViewById(R.id.accidentListView);
        adapter = new AccidentProneAdapter(this, locationList);
        listView.setAdapter(adapter);

        fetchAccidentData();
    }

    private void fetchAccidentData() {
        Map<String, AccidentLocation> locationMap = new HashMap<>();

        FirebaseDatabase.getInstance().getReference("ReportAccident").get()
                .addOnSuccessListener(snapshot -> {
                    for (DataSnapshot userSnap : snapshot.getChildren()) {
                        for (DataSnapshot tripSnap : userSnap.getChildren()) {
                            Double lat = tripSnap.child("latitude").getValue(Double.class);
                            Double lon = tripSnap.child("longitude").getValue(Double.class);
                            Object timestampObj = tripSnap.child("timestamp").getValue();

                            long timestamp = parseTimestampObject(timestampObj);

                            if (lat != null && lon != null) {
                                String key = lat + "," + lon;
                                AccidentLocation loc = locationMap.getOrDefault(key, new AccidentLocation(lat, lon));
                                loc.incrementCount();
                                loc.updateLastReported(timestamp);
                                locationMap.put(key, loc);
                            }
                        }
                    }

                    FirebaseDatabase.getInstance().getReference("TripInformation").get()
                            .addOnSuccessListener(tripSnapshot -> {
                                for (DataSnapshot userSnap : tripSnapshot.getChildren()) {
                                    for (DataSnapshot trip : userSnap.getChildren()) {
                                        DataSnapshot sosSnap = trip.child("SOS");
                                        if (sosSnap.exists()) {
                                            Double lat = sosSnap.child("latitude").getValue(Double.class);
                                            Double lon = sosSnap.child("longitude").getValue(Double.class);
                                            Object timestampObj = sosSnap.child("timestamp").getValue();

                                            long timestamp = parseTimestampObject(timestampObj);

                                            if (lat != null && lon != null) {
                                                String key = lat + "," + lon;
                                                AccidentLocation loc = locationMap.getOrDefault(key, new AccidentLocation(lat, lon));
                                                loc.incrementCount();
                                                loc.updateLastReported(timestamp);
                                                locationMap.put(key, loc);
                                            }
                                        }
                                    }
                                }

                                locationList.clear();
                                locationList.addAll(locationMap.values());
                                locationList.sort(Comparator.comparingLong(loc -> -loc.lastReported));
                                adapter.notifyDataSetChanged();
                            })
                            .addOnFailureListener(e -> showError("TripInformation fetch error: " + e.getMessage()));
                })
                .addOnFailureListener(e -> showError("ReportAccident fetch error: " + e.getMessage()));
    }

    private long parseTimestampObject(Object obj) {
        if (obj == null) return 0;

        try {
            if (obj instanceof Long) {
                return (Long) obj;
            } else if (obj instanceof String) {
                return sdf.parse((String) obj).getTime();
            }
        } catch (ParseException | ClassCastException e) {
            Log.e("AccidentProneActivity", "Timestamp parsing failed: " + e.getMessage());
        }

        return 0;
    }

    private void showError(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
        Log.e("AccidentProneActivity", msg);
    }

    public static class AccidentLocation {
        double latitude, longitude;
        int count;
        long lastReported;

        public AccidentLocation(double latitude, double longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
            this.count = 0;
            this.lastReported = 0;
        }

        public void incrementCount() {
            this.count++;
        }

        public void updateLastReported(Long timestamp) {
            if (timestamp != null && timestamp > this.lastReported) {
                this.lastReported = timestamp;
            }
        }

        public String getFormattedDate() {
            return new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(new Date(lastReported));
        }
    }
}
