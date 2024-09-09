package com.example.assetmanagementsystem.ui.locations;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.assetmanagementsystem.R;
import com.example.assetmanagementsystem.adapter.LocationsAdapter;
import com.example.assetmanagementsystem.assetdb.AssetDatabase;
import com.example.assetmanagementsystem.assetdb.model.Location;
import com.example.assetmanagementsystem.databinding.FragmentLocationsBinding;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class LocationsFragment extends Fragment implements LocationsAdapter.OnLocationItemClick {

    private FragmentLocationsBinding binding;
    protected GoogleMap googleMap;
    private RecyclerView recyclerView;
    protected AssetDatabase assetDatabase;
    protected List<Location> locations;
    protected List<Location> filteredLocations;
    protected LocationsAdapter locationsAdapter;
    private SearchView searchLocationName;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentLocationsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.fabAddLocation.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_main);
            navController.navigate(R.id.action_nav_locations_to_nav_add_location);
        });

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        locations = new ArrayList<>();
        filteredLocations = new ArrayList<>();

        locationsAdapter = new LocationsAdapter(filteredLocations, requireContext(), this);
        recyclerView.setAdapter(locationsAdapter);


        SupportMapFragment supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.google_map);
        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull GoogleMap map) {
                googleMap = map;
                LatLng starterLocation = new LatLng(44.772182, 17.191000);
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(starterLocation, 12));
                displayList();
                googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(@NonNull LatLng latLng) {

                    }
                });
            }
        });

        searchLocationName = view.findViewById(R.id.search_locationName);
        searchLocationName.setIconifiedByDefault(false);
        searchLocationName.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchByName(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchByName(newText);
                return false;
            }
        });
    }

    private void searchByName(String query) {
        filteredLocations = locations.stream()
                .filter(location -> location.getName().toLowerCase()
                        .contains(query.toLowerCase()))
                .collect(Collectors.toList());

        locationsAdapter.updateData(filteredLocations);
    }

    protected void showMarkers() {
        googleMap.clear();
        for (Location location : locations) {
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(new LatLng(location.getLatitude(), location.getLongitude()));
            markerOptions.title(location.getName());
            googleMap.addMarker(markerOptions);
        }
    }

    private void displayList() {
        assetDatabase = AssetDatabase.getInstance(requireContext());
        new LocationsAsync.RetrieveTask(this).execute();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onLocationClick(int pos) {
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(locations.get(pos).getLatitude(),
                locations.get(pos).getLongitude()), 15));
    }

    @Override
    public void deleteLocation(int pos) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Do you want to delete location " + locations.get(pos).getName() + "?")
                .setItems(new String[]{"Yes", "No"}, (dialogInterface, which) -> {
                    switch (which) {
                        case 0:
                            new LocationsAsync.DeleteTask(this,pos).execute();
                            break;
                        case 1:
                            break;
                    }
                }).show();
    }

    private void addLocationDialog(Location location) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Update Location");

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_location, null);
        builder.setView(dialogView);

        final EditText inputName = dialogView.findViewById(R.id.editTextLocationName);
        inputName.setText(location.getName());

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String locationName = inputName.getText().toString().trim();

                if (locationName.isEmpty()) {
                    Toast.makeText(requireContext(), "Location name cannot be empty", Toast.LENGTH_SHORT).show();
                } else {
                    location.setName(locationName);
                    new LocationsAsync.UpdateTask(LocationsFragment.this, location, locationsAdapter).execute();
                }
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

    @Override
    public void updateLocation(int pos) {
        addLocationDialog(locations.get(pos));
    }
}