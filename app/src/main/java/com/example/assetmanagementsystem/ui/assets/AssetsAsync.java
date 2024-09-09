package com.example.assetmanagementsystem.ui.assets;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.assetmanagementsystem.R;
import com.example.assetmanagementsystem.assetdb.helpers.EmployeeSpinnerItem;
import com.example.assetmanagementsystem.assetdb.helpers.LocationSpinnerItem;
import com.example.assetmanagementsystem.assetdb.model.Asset;
import com.example.assetmanagementsystem.assetdb.model.Employee;
import com.example.assetmanagementsystem.assetdb.model.Location;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.stream.Collectors;

public class AssetsAsync {
    protected static class RetrieveTask extends AsyncTask<Void, Void, List<Asset>> {
        private WeakReference<AssetsFragment> reference;

        RetrieveTask(AssetsFragment fragment) {
            reference = new WeakReference<>(fragment);
        }

        @Override
        protected List<Asset> doInBackground(Void... voids) {
            if (reference.get() != null)
                return reference.get().assetDatabase.getAssetDao().getAssets();
            else
                return null;
        }

        @Override
        protected void onPostExecute(List<Asset> assets) {
            AssetsFragment fragment = reference.get();
            if (fragment != null && assets != null) {
                fragment.assets.clear();
                fragment.assets.addAll(assets);
                fragment.filteredAssets.clear();
                fragment.filteredAssets.addAll(assets);
                fragment.adapter.notifyDataSetChanged();
            }
        }
    }

    protected static class RetrieveLocationsTask extends AsyncTask<Void, Void, List<Location>> {
        private WeakReference<AddAssetFragment> reference;

        RetrieveLocationsTask(AddAssetFragment fragment) {
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
            AddAssetFragment fragment = reference.get();
            if (fragment != null && locations != null) {
                fragment.locationItems = locations.stream().map(location -> new LocationSpinnerItem(location.getLocationId(), location.getName())).collect(Collectors.toList());
                fragment.locationsAdapter = new ArrayAdapter<>(fragment.getContext(),
                        R.layout.item_spinner_selected, fragment.locationItems);
                fragment.locationsAdapter.setDropDownViewResource(R.layout.item_spinner_selected);
                fragment.spinnerLocation.setAdapter(fragment.locationsAdapter);
                if (fragment.asset != null) {
                    int defaultPositionLocation = fragment.locationsAdapter.getPosition(fragment.locationItems.stream()
                            .filter(locationSpinnerItem -> locationSpinnerItem.getLocationId() == fragment.asset.getLocationId()).findFirst().orElse(null));
                    if (defaultPositionLocation != -1) {
                        fragment.spinnerLocation.setSelection(defaultPositionLocation);
                    }
                }
            }
        }
    }

    protected static class RetrieveEmployeesTask extends AsyncTask<Void, Void, List<Employee>> {
        private WeakReference<AddAssetFragment> reference;

        RetrieveEmployeesTask(AddAssetFragment fragment) {
            reference = new WeakReference<>(fragment);
        }

        @Override
        protected List<Employee> doInBackground(Void... voids) {
            if (reference.get() != null)
                return reference.get().assetDatabase.getEmployeeDao().getEmployees();
            else
                return null;
        }

        @Override
        protected void onPostExecute(List<Employee> employees) {
            AddAssetFragment fragment = reference.get();
            if (fragment != null && employees != null) {
                fragment.employeeItems = employees.stream().map(employee -> new EmployeeSpinnerItem(employee.getEmployeeId(),
                        employee.getFirstName() + " " + employee.getLastName())).collect(Collectors.toList());
                fragment.employeeAdapter = new ArrayAdapter<>(fragment.getContext(),
                        R.layout.item_spinner_selected, fragment.employeeItems);
                fragment.employeeAdapter.setDropDownViewResource(R.layout.item_spinner_selected);
                fragment.spinnerEmployee.setAdapter(fragment.employeeAdapter);
                if (fragment.asset != null) {
                    int defaultPositionEmployee = fragment.employeeAdapter.getPosition(fragment.employeeItems.stream()
                            .filter(employeeSpinnerItem -> employeeSpinnerItem.getEmployeeId() == fragment.asset.getEmployeeId()).findFirst().orElse(null));
                    if (defaultPositionEmployee != -1) {
                        fragment.spinnerEmployee.setSelection(defaultPositionEmployee);
                    }
                }
            }
        }
    }

    protected static class InsertTask extends AsyncTask<Void, Void, Boolean> {
        private WeakReference<AddAssetFragment> fragmentReference;
        private Asset asset;

        InsertTask(AddAssetFragment fragment, Asset asset) {
            fragmentReference = new WeakReference<>(fragment);
            this.asset = asset;
        }

        @Override
        protected Boolean doInBackground(Void... objs) {
            AddAssetFragment fragment = fragmentReference.get();
            if (fragment != null) {
                try {
                    if (fragment.photoUri != null) {
                        Task<Void> uploadTask = uploadImageToFirebase(fragment, fragment.photoUri, fragment.imageUrl);
                        Tasks.await(uploadTask);
                    }
                    long assetId = fragment.assetDatabase.getAssetDao().insertAsset(asset);
                    Log.e("ID ", "doInBackground: " + assetId);
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;  // Return false in case of any errors
                }
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean bool) {
            AddAssetFragment fragment = fragmentReference.get();
            if (fragment != null && bool) {
                Toast.makeText(fragment.requireContext(), fragment.getString(R.string.asset_added), Toast.LENGTH_SHORT).show();
                NavController navController = Navigation.findNavController(fragment.requireActivity(), R.id.nav_host_fragment_content_main);
                navController.navigate(R.id.action_nav_add_asset_to_nav_assets);
            }
        }
    }

    protected static class UpdateTask extends AsyncTask<Void, Void, Boolean> {
        private WeakReference<AddAssetFragment> fragmentReference;
        private Asset asset;

        UpdateTask(AddAssetFragment fragment, Asset asset) {
            fragmentReference = new WeakReference<>(fragment);
            this.asset = asset;
        }

        @Override
        protected Boolean doInBackground(Void... objs) {
            AddAssetFragment fragment = fragmentReference.get();
            if (fragment != null) {
                try {
                    if (fragment.imageChanged && fragment.photoUri != null) {
                        Task<Void> uploadTask = uploadImageToFirebase(fragment, fragment.photoUri, fragment.imageUrl);
                        Tasks.await(uploadTask);
                    }
                    fragment.assetDatabase.getAssetDao().updateAsset(asset);
                    Log.e("ID ", "asset updated");
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean bool) {
            AddAssetFragment fragment = fragmentReference.get();
            if (fragment != null && bool) {
                Toast.makeText(fragment.requireContext(), fragment.getString(R.string.asset_updated), Toast.LENGTH_SHORT).show();
                NavController navController = Navigation.findNavController(fragment.requireActivity(), R.id.nav_host_fragment_content_main);
                navController.navigate(R.id.action_nav_add_asset_to_nav_assets);
            }
        }
    }

    protected static Task<Void> uploadImageToFirebase(AddAssetFragment fragment, Uri fileUri, String imageUrl) {

        TaskCompletionSource<Void> taskCompletionSource = new TaskCompletionSource<>();

        if (fileUri != null) {
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();
            StorageReference imageRef = storageRef.child(imageUrl);

            UploadTask uploadTask = imageRef.putFile(fileUri);

            uploadTask
                    .addOnSuccessListener(taskSnapshot -> {
                        Toast.makeText(fragment.requireContext(), fragment.getString(R.string.upload_yes), Toast.LENGTH_SHORT).show();
                        taskCompletionSource.setResult(null);
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(fragment.requireContext(), fragment.getString(R.string.upload_no), Toast.LENGTH_SHORT).show();
                        taskCompletionSource.setException(e);
                    });
        } else {
            taskCompletionSource.setException(new IllegalArgumentException("File URI is null"));
        }

        return taskCompletionSource.getTask();
    }

}
