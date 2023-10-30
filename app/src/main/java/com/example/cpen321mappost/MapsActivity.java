package com.example.cpen321mappost;

import androidx.fragment.app.FragmentActivity;

import android.app.Activity;
import android.os.Bundle;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.cpen321mappost.databinding.ActivityMapsBinding;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.widget.PopupMenu;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener {

    private static final double CLICKABLE_RADIUS = 0.005 ;
    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private LocationManager locationManager;
    private static final String TAG = "MapsActivity";
    private Marker selectedMarker = null;
    private Location currentLocation;
    LatLng currentLatLng;
    public Cluster[] allClusters;
    private ArrayList<Marker> markerList;

    public interface JsonPostCallback {
        void onSuccess(Cluster[] clusters);
        void onFailure(Exception e);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        requestLocationPermissions();
        Button userProfileButton = findViewById(R.id.userProfileButton);
        userProfileButton.setOnClickListener(v -> {
            Intent intent = new Intent(MapsActivity.this, ProfileActivity.class);
            startActivity(intent);
        });

        Button moreButton = findViewById(R.id.moreButton);
        moreButton.setOnClickListener(this::showMoreOptions);
    }

    private void requestLocationPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1234);
        } else {
            startLocationUpdates();
        }
    }

    private void startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);

            Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (lastKnownLocation != null) {
                currentLocation = lastKnownLocation;
                onLocationChanged(lastKnownLocation);  // Update the map with the last known location
            }

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1234 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startLocationUpdates();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //Show all posts:
        JSONObject coordinate = new JSONObject();
        double latitude= currentLocation.getLatitude();
        double longitude= currentLocation.getLongitude();

        //TODO: for testing only put real useridafterwards
        try {
            coordinate.put("latitude", latitude);
            coordinate.put("longitude", longitude);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        getClusteredPostData(coordinate, MapsActivity.this,new JsonPostCallback() {
            @Override
            public void onSuccess(Cluster[] markers) {

                ClusterManager.getInstance().setAllClusters(markers);
                addBlueMarkersToMap(markers);

            }

            @Override
            public void onFailure(Exception e) {
                // Handle failure here
            }
        });
        mMap.setOnMarkerClickListener(marker -> {
            selectedMarker = marker;
            displayLocationMenu(marker.getPosition().latitude, marker.getPosition().longitude, "create_review_Post");
            return true; // Return true to indicate the click event has been handled
        });

        mMap.setOnMapClickListener(latLng -> {
            if (selectedMarker != null) {
                selectedMarker.remove();
            }

            boolean isNearMarker = false;
            for (Marker marker : markerList) {
                if (isNearby(marker.getPosition().latitude, marker.getPosition().longitude, latLng, CLICKABLE_RADIUS)) {
                    selectedMarker = marker;
                    displayLocationMenu(marker.getPosition().latitude, marker.getPosition().longitude, "create_review_Post");
                    isNearMarker = true;
                    break;
                }
            }

            if (!isNearMarker) {
                selectedMarker = mMap.addMarker(new MarkerOptions().position(latLng).title("Selected Location"));
                displayLocationMenu(latLng.latitude, latLng.longitude, "createPostOnly");
                selectedMarker.remove();

            }
        });



    }

     public void getClusteredPostData(JSONObject coordinate, final Activity activity, final MapsActivity.JsonPostCallback callback){

        String url = "http://4.204.251.146:8081/posts/cluster";
         HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();

         // Convert the JSONObject to query parameters
         Iterator<String> keys = coordinate.keys();
         while (keys.hasNext()) {
             String key = keys.next();
             String value = coordinate.optString(key);
             if (value != null) {
                 urlBuilder.addQueryParameter(key, value);
             }
         }
         String fullUrl = urlBuilder.build().toString();

         // Build the GET request
         Request request = new Request.Builder()
                 .url(fullUrl)
                 .build();

         OkHttpClient httpClient = HttpClient.getInstance();

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.e(TAG, "Failed to post data", e);
                        callback.onFailure(e); // Notify callback about the failure
                    }
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (response.isSuccessful()) {
                            try {
                                if (response.code() == 200) {
                                    // The operation was successful.
                                    Log.d(TAG, "Data posted successfully!");
                                    String responseData = response.body().string();
                                    Gson gson = new Gson();
                                    Cluster[] clusters = gson.fromJson(responseData, Cluster[].class);
                                    callback.onSuccess(clusters); // already on UI thread, safe to call directly


                                } else {
                                    // Handle other response codes (like 4xx or 5xx errors)
                                    IOException exception = new IOException("Unexpected response when posting data: " + response);
                                    Log.e(TAG, "Error posting data", exception);
                                    callback.onFailure(exception); // Notify callback about the failure
                                }
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            } finally {
                                response.close(); // Important to avoid leaking resources
                            }
                        } else {
                            IOException exception = new IOException("Unexpected code " + response);
                            Log.e(TAG, "Error posting data", exception);
                            callback.onFailure(exception); // Notify callback about the failure
                        }
                    }
                });
            }
        });
    }

    public void addBlueMarkersToMap(Cluster[] clusteredPosts) {
        markerList = new ArrayList<>();

        if(clusteredPosts != null) {
            for (Cluster cluster : clusteredPosts) {
                LatLng postLocation = new LatLng(cluster.getLatitude(), cluster.getLongitude());

                Marker marker = mMap.addMarker(new MarkerOptions()
                        .position(postLocation)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));

                markerList.add(marker);

            }
        }
    }
    public void showMoreOptions(View view) {
        PopupMenu moreMenu = new PopupMenu(this, view);
        moreMenu.getMenu().add("Search");
        moreMenu.getMenu().add("Tag");
        moreMenu.setOnMenuItemClickListener(item -> {
            switch (item.getTitle().toString()) {
                case "Search":
                    // Implement your Search action here
                    return true;
                case "Tag":
                    // Implement your Tag action here
                    return true;
                default:
                    return false;
            }
        });
        moreMenu.show();
    }
    private void displayLocationMenu( double latitude, double longitude, String openMode) {

        View view = getLayoutInflater().inflate(R.layout.layout_location_menu, null);
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(view);

        Button createPostButton = view.findViewById(R.id.createPostButton);
        Button reviewPostsButton = view.findViewById(R.id.reviewPostsButton);

        if (openMode.equals("createPostOnly")) {
            reviewPostsButton.setVisibility(View.GONE); // Hide the Review Posts button
        } else {
            reviewPostsButton.setVisibility(View.VISIBLE); // Show the Review Posts button
        }

        createPostButton.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
            Intent intent = new Intent(MapsActivity.this, PostActivity.class);
            intent.putExtra("latitude", Double.toString(latitude));
            intent.putExtra("longitude", Double.toString(longitude));
            startActivity(intent);
        });

        reviewPostsButton.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
            Intent intent = new Intent(MapsActivity.this, PostPreviewListActivity.class);
            intent.putExtra("mode", "reviewPosts");
            intent.putExtra("latitude", latitude);
            intent.putExtra("longitude", longitude);
            startActivity(intent);
        });
//        bottomSheetDialog.setOnDismissListener(dialog -> {
//            if (selectedMarker != null) {
//                selectedMarker.remove();
//                selectedMarker = null;
//            }
//        });

        bottomSheetDialog.show();
    }


    @Override
    public void onLocationChanged(Location location) {
        currentLocation = location;
        currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());

        if (mMap != null) {
            mMap.addMarker(new MarkerOptions().position(currentLatLng).title("Current Location"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15));
        }
}
    // Method to check if two locations are nearby based on a certain radius
    private boolean isNearby(double markeLatitude, double markerLongitude , LatLng selectedMarker, double radius) {

        float[] results = new float[1];
        Location.distanceBetween(markeLatitude, markerLongitude, selectedMarker.latitude, selectedMarker.longitude, results);
        return results[0] <= radius;
    }


}
