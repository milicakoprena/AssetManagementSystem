package com.example.assetmanagementsystem.ui.locations;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import android.Manifest;

import com.example.assetmanagementsystem.R;
import com.example.assetmanagementsystem.assetdb.AssetDatabase;
import com.example.assetmanagementsystem.assetdb.model.Location;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

public class AddLocationFragment extends Fragment {

    private Button buttonMyLocation;
    private Button buttonAddLocation;
    private GoogleMap googleMap;
    private FusedLocationProviderClient fusedLocationClient;
    private String locationName;
    private double locationLatitude;
    private double locationLongitude;
    private Location location;
    protected AssetDatabase assetDatabase;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_location, container, false);

        assetDatabase = AssetDatabase.getInstance(requireContext());

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        buttonMyLocation = view.findViewById(R.id.button_my_location);
        buttonAddLocation = view.findViewById(R.id.button_add_location);

        buttonMyLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkLocationPermissionAndShowMyLocation();
            }
        });

        buttonAddLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addLocationDialog();
            }
        });

        SupportMapFragment supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.google_map);
        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull GoogleMap map) {
                googleMap = map;
                LatLng location = new LatLng(44.772182, 17.191000);
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 10));
                googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(@NonNull LatLng latLng) {
                        handleMapClick(latLng);
                    }
                });
            }
        });

        return view;
    }

    private void addLocationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Add New Location");

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_location, null);
        builder.setView(dialogView);

        final EditText inputName = dialogView.findViewById(R.id.editTextLocationName);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                locationName = inputName.getText().toString().trim();
                if (locationName.isEmpty()) {
                    Toast.makeText(requireContext(), "Location name cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (locationLatitude == 0.0 || locationLongitude == 0.0) {
                    Toast.makeText(requireContext(), "Location coordinates are invalid", Toast.LENGTH_SHORT).show();
                    return;
                }
                Location location = new Location(locationName, locationLatitude, locationLongitude);
                new LocationsAsync.InsertTask(AddLocationFragment.this, location).execute();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void handleMapClick(LatLng latLng) {
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title(latLng.latitude + " : " + latLng.longitude);
        googleMap.clear();
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
        googleMap.addMarker(markerOptions);

        buttonAddLocation.setVisibility(View.VISIBLE);

        locationLatitude = latLng.latitude;
        locationLongitude = latLng.longitude;
    }

    private void checkLocationPermissionAndShowMyLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(requireActivity(), new OnSuccessListener<android.location.Location>() {
                    @Override
                    public void onSuccess(android.location.Location location) {
                        if (location != null && googleMap != null) {
                            LatLng myLocation = new LatLng(location.getLatitude(), location.getLongitude());
                            handleMapClick(myLocation);
                        } else {
                            Toast.makeText(requireContext(), "Unable to retrieve location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                checkLocationPermissionAndShowMyLocation();
            } else {
                Toast.makeText(requireContext(), "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }


}
