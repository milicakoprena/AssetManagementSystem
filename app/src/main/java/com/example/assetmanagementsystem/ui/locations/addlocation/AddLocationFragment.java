package com.example.assetmanagementsystem.ui.locations.addlocation;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import android.Manifest;

import com.example.assetmanagementsystem.R;
import com.example.assetmanagementsystem.assetdb.AssetDatabase;
import com.example.assetmanagementsystem.assetdb.model.Employee;
import com.example.assetmanagementsystem.assetdb.model.Location;
import com.example.assetmanagementsystem.ui.employees.addemployee.AddEmployeeFragment;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.lang.ref.WeakReference;

public class AddLocationFragment extends Fragment {

    private Button buttonMyLocation;
    private Button buttonAddLocation;
    private GoogleMap googleMap;
    private FusedLocationProviderClient fusedLocationClient;
    private String locationName;
    private double locationLatitude;
    private double locationLongitude;
    private Location location;

    private AssetDatabase assetDatabase;
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
                new InsertTask(AddLocationFragment.this, location).execute();
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
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
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
                // Permission granted, show my location
                checkLocationPermissionAndShowMyLocation();
            } else {
                // Permission denied
                Toast.makeText(requireContext(), "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private static class InsertTask extends AsyncTask<Void, Void, Boolean> {
        private WeakReference<AddLocationFragment> fragmentReference;
        private Location location;

        InsertTask(AddLocationFragment fragment, Location location) {
            fragmentReference = new WeakReference<>(fragment);
            this.location = location;
        }

        @Override
        protected Boolean doInBackground(Void... objs) {
            AddLocationFragment fragment = fragmentReference.get();
            if (fragment != null) {
                long j = fragment.assetDatabase.getLocationDao().insertLocation(location);
                location.setLocationId(j);
                Log.e("ID ", "doInBackground: " + j);
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean bool) {
            AddLocationFragment fragment = fragmentReference.get();
            if (fragment != null && bool) {
                Toast.makeText(fragment.requireContext(), "Location added successfully", Toast.LENGTH_SHORT).show();
                NavController navController = Navigation.findNavController(fragment.requireActivity(), R.id.nav_host_fragment_content_main);
                navController.navigate(R.id.action_nav_add_location_to_nav_locations);
            }
        }
    }
}
