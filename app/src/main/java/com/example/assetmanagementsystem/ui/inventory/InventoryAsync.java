package com.example.assetmanagementsystem.ui.inventory;

import android.os.AsyncTask;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.assetmanagementsystem.R;
import com.example.assetmanagementsystem.assetdb.helpers.EmployeeSpinnerItem;
import com.example.assetmanagementsystem.assetdb.helpers.InventoryDetails;
import com.example.assetmanagementsystem.assetdb.helpers.LocationSpinnerItem;
import com.example.assetmanagementsystem.assetdb.model.Asset;
import com.example.assetmanagementsystem.assetdb.model.Employee;
import com.example.assetmanagementsystem.assetdb.model.Inventory;
import com.example.assetmanagementsystem.assetdb.model.Location;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.stream.Collectors;

public class InventoryAsync {
    protected static class RetrieveLocationsTask extends AsyncTask<Void, Void, List<Location>> {
        private WeakReference<AddInventoryFragment> reference;

        RetrieveLocationsTask(AddInventoryFragment fragment) {
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
            AddInventoryFragment fragment = reference.get();
            if (fragment != null && locations != null) {
                fragment.locationItems = locations.stream().map(location -> new LocationSpinnerItem(location.getLocationId(), location.getName())).collect(Collectors.toList());
                fragment.locationsAdapter = new ArrayAdapter<>(fragment.getContext(),
                        R.layout.item_spinner_selected, fragment.locationItems);
                fragment.locationsAdapter.setDropDownViewResource(R.layout.item_spinner_selected);
                fragment.spinnerLocation.setAdapter(fragment.locationsAdapter);
            }
        }
    }

    protected static class RetrieveEmployeesTask extends AsyncTask<Void, Void, List<Employee>> {
        private WeakReference<AddInventoryFragment> reference;

        RetrieveEmployeesTask(AddInventoryFragment fragment) {
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
            AddInventoryFragment fragment = reference.get();
            if (fragment != null && employees != null) {
                fragment.employeeItems = employees.stream().map(employee -> new EmployeeSpinnerItem(employee.getEmployeeId(),
                        employee.getFirstName() + " " + employee.getLastName())).collect(Collectors.toList());
                fragment.employeeAdapter = new ArrayAdapter<>(fragment.getContext(),
                        R.layout.item_spinner_selected, fragment.employeeItems);
                fragment.employeeAdapter.setDropDownViewResource(R.layout.item_spinner_selected);
                fragment.spinnerEmployee.setAdapter(fragment.employeeAdapter);
            }
        }
    }

    protected static class RetrieveAssetByIdTask extends AsyncTask<Void, Void, Asset> {
        private WeakReference<AddInventoryFragment> reference;
        private long barcode;

        RetrieveAssetByIdTask(AddInventoryFragment fragment, long barcode) {
            reference = new WeakReference<>(fragment);
            this.barcode = barcode;
        }

        @Override
        protected Asset doInBackground(Void... voids) {
            if (reference.get() != null)
                return reference.get().assetDatabase.getAssetDao().findById(barcode);
            else
                return null;
        }

        @Override
        protected void onPostExecute(Asset asset) {
            AddInventoryFragment fragment = reference.get();
            if (fragment != null) {
                if (asset != null) {
                    Toast.makeText(fragment.requireContext(), fragment.getString(R.string.asset) +
                            " " + asset.getName() + " " + fragment.getString(R.string.found), Toast.LENGTH_SHORT).show();
                    String employeeName = fragment.employeeItems.stream()
                            .filter(employeeSpinnerItem -> employeeSpinnerItem.getEmployeeId() == asset.getEmployeeId()).findFirst().orElse(null).getName();
                    fragment.twEmployee.append(": " + employeeName);
                    String locationName = fragment.locationItems.stream()
                            .filter(locationSpinnerItem -> locationSpinnerItem.getLocationId() == asset.getLocationId()).findFirst().orElse(null).getName();
                    fragment.twLocation.append(": " + locationName);
                    fragment.asset = asset;
                    if (fragment.update) {
                        LinearLayout layoutImageBarcode = fragment.getView().findViewById(R.id.layout_imageBarcode);
                        LinearLayout layoutBarcode = fragment.getView().findViewById(R.id.layout_barcode);
                        layoutImageBarcode.setVisibility(View.VISIBLE);
                        layoutBarcode.setVisibility(View.GONE);

                        TextView twBarcode = fragment.getView().findViewById(R.id.tw_barcode);
                        TextView twName = fragment.getView().findViewById(R.id.tw_name);
                        twBarcode.setText(String.valueOf(asset.getBarcode()));
                        twName.setText(asset.getName());

                        ImageView imageView = fragment.getView().findViewById(R.id.image);
                        fragment.loadImage(asset.getImageUrl(), imageView);
                    }
                } else {
                    Toast.makeText(fragment.requireContext(), fragment.getText(R.string.asset_not_found), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


    protected static class InsertTask extends AsyncTask<Void, Void, Boolean> {
        private WeakReference<AddInventoryFragment> fragmentReference;
        private Inventory inventory;
        private Asset asset;

        InsertTask(AddInventoryFragment fragment, Inventory inventory, Asset asset) {
            fragmentReference = new WeakReference<>(fragment);
            this.inventory = inventory;
            this.asset = asset;
        }

        @Override
        protected Boolean doInBackground(Void... objs) {
            AddInventoryFragment fragment = fragmentReference.get();
            if (fragment != null) {
                try {
                    return fragment.assetDatabase.getInventoryAssetDao().insertInventoryAndUpdateAsset(inventory, asset);
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            AddInventoryFragment fragment = fragmentReference.get();
            if (fragment != null) {
                if (success) {
                    Toast.makeText(fragment.requireContext(), fragment.getString(R.string.inv_added_asset_updated), Toast.LENGTH_SHORT).show();
                    NavController navController = Navigation.findNavController(fragment.requireActivity(), R.id.nav_host_fragment_content_main);
                    navController.navigate(R.id.action_nav_add_inventory_to_nav_inventory);
                } else {
                    Toast.makeText(fragment.requireContext(), fragment.getString(R.string.transaction_fail), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    protected static class UpdateTask extends AsyncTask<Void, Void, Boolean> {
        private WeakReference<AddInventoryFragment> fragmentReference;
        private Inventory inventory;
        private Asset asset;

        UpdateTask(AddInventoryFragment fragment, Inventory inventory, Asset asset) {
            fragmentReference = new WeakReference<>(fragment);
            this.inventory = inventory;
            this.asset = asset;
        }

        @Override
        protected Boolean doInBackground(Void... objs) {
            AddInventoryFragment fragment = fragmentReference.get();
            if (fragment != null) {
                try {
                    return fragment.assetDatabase.getInventoryAssetDao().updateInventoryAndUpdateAsset(inventory, asset);
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            AddInventoryFragment fragment = fragmentReference.get();
            if (fragment != null) {
                if (success) {
                    Toast.makeText(fragment.requireContext(), fragment.getString(R.string.inv_updated_asset_updated), Toast.LENGTH_SHORT).show();
                    NavController navController = Navigation.findNavController(fragment.requireActivity(), R.id.nav_host_fragment_content_main);
                    navController.navigate(R.id.action_nav_add_inventory_to_nav_inventory);
                } else {
                    Toast.makeText(fragment.requireContext(), fragment.getString(R.string.transaction_fail), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    protected static class RetrieveTask extends AsyncTask<Void, Void, List<InventoryDetails>> {
        private WeakReference<InventoryFragment> reference;

        RetrieveTask(InventoryFragment fragment) {
            reference = new WeakReference<>(fragment);
        }

        @Override
        protected List<InventoryDetails> doInBackground(Void... voids) {
            if (reference.get() != null) {
                List<Inventory> inventories = reference.get().assetDatabase.getInventoryDao().getInventories();
                List<InventoryDetails> inventoryDetails = inventories.stream().map(inventory -> {
                    Asset asset = reference.get().assetDatabase.getInventoryDao().getAssetById(inventory.getBarcode());
                    Employee oldEmployee = reference.get().assetDatabase.getInventoryDao().getEmployeeById(inventory.getOldEmployeeId());
                    Employee newEmployee = reference.get().assetDatabase.getInventoryDao().getEmployeeById(inventory.getNewEmployeeId());
                    Location oldLocation = reference.get().assetDatabase.getInventoryDao().getLocationById(inventory.getOldLocationId());
                    Location newLocation = reference.get().assetDatabase.getInventoryDao().getLocationById(inventory.getNewLocationId());
                    return new InventoryDetails(inventory, asset,
                            oldEmployee.getFirstName() + " " + oldEmployee.getLastName(),
                            newEmployee.getFirstName() + " " + newEmployee.getLastName(),
                            oldLocation.getName(), newLocation.getName());
                }).collect(Collectors.toList());
                return inventoryDetails;
            } else
                return null;
        }

        @Override
        protected void onPostExecute(List<InventoryDetails> inventoryDetails) {
            InventoryFragment fragment = reference.get();
            if (fragment != null && inventoryDetails != null) {
                fragment.inventoryDetails.clear();
                fragment.inventoryDetails.addAll(inventoryDetails);
                fragment.filteredInventoryDetails.clear();
                fragment.filteredInventoryDetails.addAll(inventoryDetails);
                fragment.inventoryAdapter.notifyDataSetChanged();
            }
        }
    }

    protected static class DeleteTask extends AsyncTask<Void, Void, Void> {
        private WeakReference<InventoryFragment> reference;
        private int pos;

        public DeleteTask(InventoryFragment fragment, int pos) {
            reference = new WeakReference<>(fragment);
            this.pos = pos;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            reference.get().assetDatabase.getInventoryDao().deleteInventoryById(reference.get().inventoryDetails.get(pos).getInventory().getInventoryId());
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            reference.get().inventoryDetails.remove(pos);
            reference.get().inventoryAdapter.notifyItemRemoved(pos);
            Toast.makeText(reference.get().requireContext(), reference.get().getString(R.string.inv_deleted), Toast.LENGTH_SHORT).show();
        }
    }


}
