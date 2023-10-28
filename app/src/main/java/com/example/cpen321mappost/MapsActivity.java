package com.example.cpen321mappost;

import androidx.fragment.app.FragmentActivity;
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

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private LocationManager locationManager;
    private static final String TAG = "MapsActivity";
    private Marker selectedMarker = null;
    private Location currentLocation;
    LatLng currentLatLng;

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
        Post[] clusteredPosts=null;
//        Post[] clusteredPosts = get from backend of all posts
        addBlueMarkersToMap(clusteredPosts);



    }

    public void addBlueMarkersToMap(Post[]  clusteredPosts) {
        if(clusteredPosts != null) {
            for (Post post : clusteredPosts) {

                LatLng postLocation = new LatLng(post.getLatitude(), post.getLongitude());
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
        bottomSheetDialog.show();



    }

    @Override
    public void onLocationChanged(Location location) {
        currentLocation=location;

        LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.addMarker(new MarkerOptions().position(currentLatLng).title("Current Location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15));
    }



}
