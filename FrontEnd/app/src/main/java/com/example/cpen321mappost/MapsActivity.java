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
import android.os.Handler;
import android.widget.PopupMenu;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.gson.Gson;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;



public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener {

    private static final double CLICKABLE_RADIUS = 0.005 ;
    private GoogleMap mMap;
    private LocationManager locationManager;
    private static final String TAG = "MapsActivity";
    private Marker selectedMarker = null;
    private Location currentLocation;
    public LatLng currentLatLng;
    private ArrayList<Marker> markerList= null;
    private boolean isPermissionGranted =false;
    public static boolean TEST_MODE = false;

    //ChatGPT usage: Yes
    public interface JsonPostCallback {
        void onSuccess(Cluster[] clusters);
        void onFailure(Exception e);
    }

    // TODO: implement onResume()

    //ChatGPT usage: Partial
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityMapsBinding binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        assert mapFragment != null;
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

    //ChatGPT usage: No
    private void requestLocationPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1234);
        } else {
            startLocationUpdates();
        }
    }

    //ChatGPT usage: No
    private void startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);

            Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (lastKnownLocation != null) {
                currentLocation = lastKnownLocation;
                onLocationChanged(lastKnownLocation);  // Update the map with the last known location
                isPermissionGranted =true;
            }

        }
    }

    //ChatGPT usage: Partial
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1234 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startLocationUpdates();
            initializeBlueMarkers();
        }
    }

    //ChatGPT usage: Partial
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        if(isPermissionGranted )
        {
            initializeBlueMarkers();
        }


        mMap.setOnMarkerClickListener(marker -> {
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
//                selectedMarker.remove();
            }
        });
    }

    public void initializeBlueMarkers()
    {

        //Show all posts:
        JSONObject coordinate = new JSONObject();
        double latitude= currentLocation.getLatitude();
        double longitude= currentLocation.getLongitude();

        //TODO: for testing only put real useridafterwards
        try {
            coordinate.put("latitude", latitude);
            coordinate.put("longitude", longitude);
        } catch (JSONException e) {
            Log.e(TAG, "FAILURE initializeBlueMarkers: " + e);
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
                Toast.makeText(MapsActivity.this, "Failed to get nearby blue markers on map", Toast.LENGTH_SHORT).show();
                final Toast toast = Toast.makeText(MapsActivity.this, "Failed to get nearby blue markers on map", Toast.LENGTH_LONG);
                final Handler handler = new Handler();
                handler.postDelayed(toast::show, 3000); // 3000ms delay to show the toast again after the initial showing
            }
        });

    }

    //ChatGPT usage: Partial
     public static void getClusteredPostData(JSONObject coordinate, final Activity activity, final MapsActivity.JsonPostCallback callback){

         String url = "http://4.204.251.146:8081/posts/cluster";
         OkHttpClient httpClient = HttpClient.getInstance();
         HttpUrl.Builder urlBuilder = Objects.requireNonNull(HttpUrl.parse(url)).newBuilder();

         Iterator<String> keys = coordinate.keys();
         while (keys.hasNext()) {
             String key = keys.next();
             String value = coordinate.optString(key);
             urlBuilder.addQueryParameter(key, value);
         }
         String fullUrl = urlBuilder.build().toString();

         Request request = new Request.Builder()
                 .url(fullUrl)
                 .build();

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                activity.runOnUiThread(() -> {

                    Log.e(TAG, "FAILURE GET CLUSTERED POSTS: " + e);

                    callback.onFailure(e);

                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                // Read the response in the background thread
                String responseData = null;
                if (!response.isSuccessful()) {
                    Log.d(TAG, "Unexpected server response, the code is: " + response.code());
                } else {
                    try {
                        responseData = response.body().string(); // This is executed in background
                    } catch (IOException e) {
                        Log.e(TAG, e.toString());
                    }
                }

                // Final response data for UI
                String finalResponseData = responseData;

                // Switch to the main thread for UI update
                activity.runOnUiThread(() -> {
                    if (finalResponseData == null) {
                        callback.onFailure(new IOException("Failed to read response"));
                    } else {
                        Log.d(TAG, "GET CLUSTERED POSTS SUCCEED");
                        Gson gson = new Gson();
                        Cluster[] clusters = gson.fromJson(finalResponseData, Cluster[].class);
                        callback.onSuccess(clusters);
                    }
                });
            }
        });
    }

    //ChatGPT usage: Partial
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

    //ChatGPT usage: Partial
    public void showMoreOptions(View view) {

        PopupMenu moreMenu = new PopupMenu(this, view);
        moreMenu.getMenu().add("Search");
        moreMenu.getMenu().add("Tag");

        moreMenu.setOnMenuItemClickListener(item -> {

            switch (Objects.requireNonNull(item.getTitle()).toString()) {

                case "Search":

                    Intent searchIntent= new Intent(MapsActivity.this, SearchActivity.class);
                    startActivity(searchIntent);
                    return true;

                case "Tag":

                    Intent tagIntent= new Intent(MapsActivity.this, TagActivity.class);
                    double latitude= currentLocation.getLatitude();
                    double longitude= currentLocation.getLongitude();
                    tagIntent.putExtra("latitude", Double.toString(latitude));
                    tagIntent.putExtra("longitude", Double.toString(longitude));
                    startActivity(tagIntent);
                    return true;

                default:

                    return false;

            }
        });

        moreMenu.show();

    }

    //ChatGPT usage: Partial
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
            if(TEST_MODE)
            {
                double Mocklatitude = 37.42553124314847;
                double Mocklongitude = -122.07808557897808;
                intent.putExtra("latitude", Double.toString(Mocklatitude));
                intent.putExtra("longitude", Double.toString(Mocklongitude));
                startActivity(intent);
            }else {
                intent.putExtra("latitude", Double.toString(latitude));
                intent.putExtra("longitude", Double.toString(longitude));
                startActivity(intent);
            }
        });

        reviewPostsButton.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
            Intent intent = new Intent(MapsActivity.this, PostPreviewListActivity.class);
            intent.putExtra("mode", "reviewPosts");

            if(TEST_MODE)
            {
                double Mocklatitude = 37.42553124314847;
                double Mocklongitude = -122.07808557897808;
                intent.putExtra("latitude", Mocklatitude);
                intent.putExtra("longitude", Mocklongitude);
                startActivity(intent);
                startActivity(intent);
            }else {
                intent.putExtra("latitude", latitude);
                intent.putExtra("longitude", longitude);
                startActivity(intent);
            }

        });

        bottomSheetDialog.show();
    }

    //ChatGPT usage: Partial
    @Override
    public void onLocationChanged(Location location) {
        currentLocation = location;
        currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());

        if (mMap != null) {
            mMap.addMarker(new MarkerOptions().position(currentLatLng).title("Current Location"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15));
        }
}
    //ChatGPT usage: Partial
    private boolean isNearby(double markeLatitude, double markerLongitude , LatLng selectedMarker, double radius) {

        float[] results = new float[1];
        Location.distanceBetween(markeLatitude, markerLongitude, selectedMarker.latitude, selectedMarker.longitude, results);
        return results[0] <= radius;
    }

    public void refreshPage(View view) {

        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
        finish();

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (currentLocation != null) {
            JSONObject coordinate = new JSONObject();
            try {
                coordinate.put("latitude", currentLocation.getLatitude());
                coordinate.put("longitude", currentLocation.getLongitude());
                getClusteredPostData(coordinate, MapsActivity.this, new JsonPostCallback() {
                    @Override
                    public void onSuccess(Cluster[] clusters) {
                        // Update your map with the new data
                        addBlueMarkersToMap(clusters);
                    }

                    @Override
                    public void onFailure(Exception e) {
                        // Handle failure
                        Toast.makeText(MapsActivity.this, "Failed to refresh map data", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (JSONException e) {
                Log.e(TAG, "FAILURE refreshMapData: " + e);
            }
        }
    }



}
