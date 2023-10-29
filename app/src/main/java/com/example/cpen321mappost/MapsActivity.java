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

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private LocationManager locationManager;
    private static final String TAG = "MapsActivity";
    private Marker selectedMarker = null;
    private Location currentLocation;
    LatLng currentLatLng;
    public Cluster[] allClusters;

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


        mMap.setOnMapClickListener(latLng -> {
            if (selectedMarker != null) {
                selectedMarker.remove();
            }
            selectedMarker = mMap.addMarker(new MarkerOptions().position(latLng).title("Selected Location"));
        });

        //Show dropdown menu if user click on a location that already has a marker:
        mMap.setOnMarkerClickListener(marker -> {
            if (selectedMarker != null && marker.equals(selectedMarker)) {
                displayLocationMenu(marker.getPosition());
                return true;
            }
            return false;
        });

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
            public void onSuccess(Cluster[] clusters) {

                ClusterManager.getInstance().setAllClusters(clusters);
                addBlueMarkersToMap(clusters);

            }

            @Override
            public void onFailure(Exception e) {
                // Handle failure here
            }
        });

    }

     public void getClusteredPostData(JSONObject postData, final Activity activity, final MapsActivity.JsonPostCallback callback){

        String url = "http://4.204.251.146:8081/posts/cluster";
         HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();

         // Convert the JSONObject to query parameters
         Iterator<String> keys = postData.keys();
         while (keys.hasNext()) {
             String key = keys.next();
             String value = postData.optString(key);
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
        if(clusteredPosts != null) {
            for (Cluster cluster : clusteredPosts) {

                LatLng postLocation = new LatLng(cluster.getLatitude(), cluster.getLongitude());
                mMap.addMarker(new MarkerOptions()
                        .position(postLocation)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));

            }
        }
    }

    private void displayLocationMenu(LatLng latLng) {
        View view = getLayoutInflater().inflate(R.layout.layout_location_menu, null);
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(view);


        Button createPostButton = view.findViewById(R.id.createPostButton);
        createPostButton.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
        });

        Button reviewPostsButton = view.findViewById(R.id.reviewPostsButton);
        reviewPostsButton.setOnClickListener(v -> {
            bottomSheetDialog.dismiss(); // Close the bottom sheet when the button is clicked
        });


        //Click on Create Post
        createPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent =new Intent(MapsActivity.this,PostActivity.class);
                intent.putExtra("latitude",Double.toString(latLng.latitude));
                intent.putExtra("longitude",Double.toString(latLng.longitude));

                startActivity(intent);
            }
        });

        //Click on Review Post
        reviewPostsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent =new Intent(MapsActivity.this,PostPreviewListActivity.class);
                intent.putExtra("latitude",Double.toString(latLng.latitude));
                intent.putExtra("longitude",Double.toString(latLng.longitude));
                //Pass the current cluster latitude longtitude,
                //In PostPreviewListActivity, search for the destinated cluster; Then render the content


                startActivity(intent);
            }
        });
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



}
