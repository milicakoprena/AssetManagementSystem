package com.example.assetmanagementsystem.ui.locations;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.assetmanagementsystem.R;
import com.example.assetmanagementsystem.adapter.LocationsAdapter;
import com.example.assetmanagementsystem.assetdb.model.Asset;
import com.example.assetmanagementsystem.assetdb.model.Location;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class LocationsAsync {
    protected static class RetrieveTask extends AsyncTask<Void, Void, List<Location>> {
        private WeakReference<LocationsFragment> reference;

        RetrieveTask(LocationsFragment fragment) {
            reference = new WeakReference<>(fragment);
        }

        @Override
        protected List<Location> doInBackground(Void... voids) {
            if (reference.get() != null)
                return reference.get().assetDatabase.getLocationDao().getLocations();
            else
                return null;
        }

        @Override
        protected void onPostExecute(List<Location> locations) {
            LocationsFragment fragment = reference.get();
            if (fragment != null && locations != null) {
                fragment.locations.clear();
                fragment.locations.addAll(locations);
                fragment.filteredLocations.clear();
                fragment.filteredLocations.addAll(locations);
                fragment.locationsAdapter.notifyDataSetChanged();

                if (fragment.googleMap != null) {
                    fragment.showMarkers();
                }
            }
        }
    }

    protected static class RetrieveAssetsByLocationTask extends AsyncTask<Void, Void, List<Asset>> {
        private WeakReference<LocationsFragment> reference;
        private long locationId;
        private String locationName;

        RetrieveAssetsByLocationTask(LocationsFragment fragment, long locationId, String locationName) {
            reference = new WeakReference<>(fragment);
            this.locationId = locationId;
            this.locationName = locationName;
        }

        @Override
        protected List<Asset> doInBackground(Void... voids) {
            if (reference.get() != null)
                return reference.get().assetDatabase.getAssetDao().findByLocation(locationId);
            else
                return null;
        }

        @Override
        protected void onPostExecute(List<Asset> assets) {
            LocationsFragment fragment = reference.get();
            if (fragment != null && assets != null) {
                Map<Long, String> assetMap = assets.stream()
                        .collect(Collectors.toMap(Asset::getBarcode, Asset::getName));
                fragment.markerAssets.clear();
                fragment.markerAssets.putAll(assetMap);
                showAssetsDialog(fragment, fragment.markerAssets);
            }
        }

        private void showAssetsDialog(LocationsFragment fragment, Map<Long, String> assetMap) {
            AlertDialog.Builder builder = new AlertDialog.Builder(fragment.getContext());
            builder.setTitle("Assets at location " + locationName);
            List<String> assetDetails = new ArrayList<>();
            for (Map.Entry<Long, String> entry : assetMap.entrySet()) {
                String assetDetail = entry.getKey() + " - " + entry.getValue();
                assetDetails.add(assetDetail);
            }

            CharSequence[] assetsArray = assetDetails.toArray(new CharSequence[assetDetails.size()]);

            builder.setItems(assetsArray, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });

            builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.show();
        }
    }

    protected static class DeleteTask extends AsyncTask<Void, Void, Void> {
        private WeakReference<LocationsFragment> reference;
        private int pos;

        DeleteTask(LocationsFragment fragment, int pos) {
            reference = new WeakReference<>(fragment);
            this.pos = pos;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            reference.get().assetDatabase.getLocationDao().deleteLocation(reference.get().locations.get(pos));
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            reference.get().locations.remove(pos);
            reference.get().locationsAdapter.notifyItemRemoved(pos);
            Toast.makeText(reference.get().requireContext(), "Location deleted", Toast.LENGTH_SHORT).show();
            reference.get().showMarkers();
        }
    }


    protected static class UpdateTask extends AsyncTask<Void, Void, Boolean> {
        private WeakReference<LocationsFragment> fragmentReference;
        private Location location;
        private LocationsAdapter locationsAdapter;

        UpdateTask(LocationsFragment fragment, Location location, LocationsAdapter locationsAdapter) {
            fragmentReference = new WeakReference<>(fragment);
            this.location = location;
            this.locationsAdapter = locationsAdapter;
        }

        @Override
        protected Boolean doInBackground(Void... objs) {
            LocationsFragment fragment = fragmentReference.get();
            if (fragment != null) {
                fragment.assetDatabase.getLocationDao().updateLocation(location);
                Log.e("ID ", "location updated");
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean bool) {
            LocationsFragment fragment = fragmentReference.get();
            if (fragment != null && bool) {
                Toast.makeText(fragment.requireContext(), "Location updated successfully", Toast.LENGTH_SHORT).show();
                locationsAdapter.notifyDataSetChanged();
            }
        }
    }

    protected static class InsertTask extends AsyncTask<Void, Void, Boolean> {
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
