package com.digital.compass.maps.digitalcompass.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.digital.compass.maps.digitalcompass.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.jetbrains.annotations.NotNull;

public class MapFragment extends Fragment {

    public static GoogleMap map;
    double lat;
    double lon;
    public static UiSettings mUiSettings;

    /*public MapFragment(double lat, double lon) {
        this.lat = lat;
        this.lon = lon;
    }*/

    public static MapFragment newInstance(double lat, double lon) {
        MapFragment myFragment = new MapFragment();

        Bundle args = new Bundle();
        args.putDouble("lat", lat);
        args.putDouble("lon", lon);
        myFragment.setArguments(args);

        return myFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        lat = getArguments().getDouble("lat", 21.04088715582817);
        lon = getArguments().getDouble("lon", 105.76411645538103);

        SupportMapFragment supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.google_map);
        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @SuppressLint("MissingPermission")
            @Override
            public void onMapReady(@NonNull @NotNull GoogleMap googleMap) {
                map = googleMap;
                map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);

                map.setMyLocationEnabled(true);

                LatLng address = new LatLng(lat, lon);
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(address, 17));

                //set control button
                mUiSettings = map.getUiSettings();
                mUiSettings.setAllGesturesEnabled(false);
                mUiSettings.setCompassEnabled(true);
                mUiSettings.setMapToolbarEnabled(true);

                googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(@NonNull @NotNull LatLng latLng) {
                        MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions.position(latLng);
                        markerOptions.title(latLng.latitude + " : " + latLng.longitude);
                        googleMap.clear();
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                        googleMap.addMarker(markerOptions);
                    }
                });
            }
        });
        return view;
    }
}