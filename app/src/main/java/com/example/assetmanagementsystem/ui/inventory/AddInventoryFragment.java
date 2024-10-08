package com.example.assetmanagementsystem.ui.inventory;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.bumptech.glide.request.RequestOptions;
import com.example.assetmanagementsystem.R;
import com.example.assetmanagementsystem.assetdb.AssetDatabase;
import com.example.assetmanagementsystem.assetdb.helpers.EmployeeSpinnerItem;
import com.example.assetmanagementsystem.assetdb.helpers.LocationSpinnerItem;
import com.example.assetmanagementsystem.assetdb.model.Asset;
import com.example.assetmanagementsystem.assetdb.model.Inventory;
import com.example.assetmanagementsystem.glide.GlideApp;
import com.example.assetmanagementsystem.util.CameraUtil;
import com.example.assetmanagementsystem.util.Constants;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.List;


public class AddInventoryFragment extends Fragment {
    protected AssetDatabase assetDatabase;
    protected List<LocationSpinnerItem> locationItems;
    protected List<EmployeeSpinnerItem> employeeItems;
    protected ArrayAdapter<LocationSpinnerItem> locationsAdapter;
    protected ArrayAdapter<EmployeeSpinnerItem> employeeAdapter;
    protected Spinner spinnerLocation;
    protected Spinner spinnerEmployee;
    private Button buttonScan;
    private Button buttonAdd;
    private Button buttonFind;
    private LocationSpinnerItem selectedLocationItem;
    private EmployeeSpinnerItem selectedEmployeeItem;
    private TextInputEditText editTextBarcode;
    protected TextView twEmployee;
    protected TextView twLocation;
    private Switch swEmployee;
    private Switch swLocation;

    protected Asset asset;
    private Inventory inventory;
    protected boolean update = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_add_inventory, container, false);

        spinnerLocation = rootView.findViewById(R.id.spinner_location);
        spinnerEmployee = rootView.findViewById(R.id.spinner_employee);

        twEmployee = rootView.findViewById(R.id.tw_current_employee);
        twLocation = rootView.findViewById(R.id.tw_current_location);
        spinnerEmployee.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedEmployeeItem = (EmployeeSpinnerItem) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spinnerLocation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedLocationItem = (LocationSpinnerItem) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        locationItems = new ArrayList<>();
        displayLocations();

        employeeItems = new ArrayList<>();
        displayEmployees();


        swEmployee = rootView.findViewById(R.id.sw_employee);
        swLocation = rootView.findViewById(R.id.sw_location);

        swEmployee.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (asset != null) {
                if (isChecked) {
                    int defaultPositionEmployee = employeeAdapter.getPosition(employeeItems.stream()
                            .filter(employeeSpinnerItem -> employeeSpinnerItem.getEmployeeId() == asset.getEmployeeId()).findFirst().orElse(null));
                    if (defaultPositionEmployee != -1) {
                        spinnerEmployee.setSelection(defaultPositionEmployee);
                    }
                }
            }
        });

        swLocation.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (asset != null) {
                if (isChecked) {
                    int defaultPositionLocation = locationsAdapter.getPosition(locationItems.stream()
                            .filter(locationSpinnerItem -> locationSpinnerItem.getLocationId() == asset.getLocationId()).findFirst().orElse(null));
                    if (defaultPositionLocation != -1) {
                        spinnerLocation.setSelection(defaultPositionLocation);
                    }
                }
            }
        });

        buttonAdd = rootView.findViewById(R.id.button_add);

        Bundle bundle = getArguments();
        if (bundle != null) {
            inventory = bundle.getParcelable("inventory");
            if (inventory != null) {
                update = true;
                findAssetById(String.valueOf(inventory.getBarcode()));
                buttonAdd.setText(getString(R.string.update_inventory));
                buttonAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (asset != null && selectedEmployeeItem != null && selectedLocationItem != null) {
                            inventory.setOldEmployeeId(asset.getEmployeeId());
                            inventory.setOldLocationId(asset.getLocationId());
                            inventory.setNewEmployeeId(selectedEmployeeItem.getEmployeeId());
                            inventory.setNewLocationId(selectedLocationItem.getLocationId());

                            asset.setEmployeeId(selectedEmployeeItem.getEmployeeId());
                            asset.setLocationId(selectedLocationItem.getLocationId());

                            new InventoryAsync.UpdateTask(AddInventoryFragment.this, inventory, asset).execute();
                        } else
                            Toast.makeText(requireContext(), getString(R.string.missing_fields), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } else {
            buttonScan = rootView.findViewById(R.id.button_scan);
            editTextBarcode = rootView.findViewById(R.id.et_barcode);
            buttonScan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    handleScanning();
                }
            });

            buttonFind = rootView.findViewById(R.id.button_find);
            buttonFind.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String barcode = editTextBarcode.getText().toString();
                    findAssetById(barcode);
                }
            });
            buttonAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!editTextBarcode.getText().toString().isEmpty()
                            && selectedLocationItem != null && selectedEmployeeItem != null) {
                        inventory = new Inventory(Long.parseLong(editTextBarcode.getText().toString()), asset.getEmployeeId(),
                                selectedEmployeeItem.getEmployeeId(), asset.getLocationId(),
                                selectedLocationItem.getLocationId());
                        if (asset.getEmployeeId() != selectedEmployeeItem.getEmployeeId())
                            asset.setEmployeeId(selectedEmployeeItem.getEmployeeId());
                        if (asset.getLocationId() != selectedLocationItem.getLocationId())
                            asset.setLocationId(selectedLocationItem.getLocationId());
                        new InventoryAsync.InsertTask(AddInventoryFragment.this, inventory, asset).execute();
                    } else
                        Toast.makeText(requireContext(), getString(R.string.missing_fields), Toast.LENGTH_SHORT).show();
                }
            });
        }
        return rootView;
    }


    protected void loadImage(String imageUrl, ImageView imageView) {
        if (imageView != null) {
            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(imageUrl);
            RequestOptions requestOptions = new RequestOptions()
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder);

            GlideApp.with(this)
                    .load(storageReference)
                    .apply(requestOptions)
                    .into(imageView);

        }
    }

    private void findAssetById(String barcode) {
        if (!barcode.isEmpty())
            new InventoryAsync.RetrieveAssetByIdTask(this, Long.parseLong(barcode)).execute();
        else Toast.makeText(requireContext(), getString(R.string.enter_barcode), Toast.LENGTH_SHORT).show();
    }

    private void displayLocations() {
        assetDatabase = AssetDatabase.getInstance(requireContext());
        new InventoryAsync.RetrieveLocationsTask(this).execute();
    }

    private void displayEmployees() {
        assetDatabase = AssetDatabase.getInstance(requireContext());
        new InventoryAsync.RetrieveEmployeesTask(this).execute();
    }

    private void handleScanning() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, Constants.REQUEST_CODE_PERMISSION_QR);
            return;
        }
        CameraUtil.startScanning(this);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Constants.REQUEST_CODE_PERMISSION_QR) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                CameraUtil.startScanning(this);
            } else {
                Toast.makeText(requireContext(), getString(R.string.camera_denied), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (intentResult != null) {
            if (intentResult.getContents() == null) {
                Toast.makeText(getContext(), getString(R.string.cancelled), Toast.LENGTH_SHORT).show();
            } else {
                editTextBarcode.setText(intentResult.getContents());
            }
        }
    }

}