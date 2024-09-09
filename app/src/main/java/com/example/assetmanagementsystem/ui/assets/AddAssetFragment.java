package com.example.assetmanagementsystem.ui.assets;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
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
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.request.RequestOptions;
import com.example.assetmanagementsystem.R;
import com.example.assetmanagementsystem.assetdb.AssetDatabase;
import com.example.assetmanagementsystem.assetdb.enums.AssetCategoryEnum;
import com.example.assetmanagementsystem.assetdb.helpers.EmployeeSpinnerItem;
import com.example.assetmanagementsystem.assetdb.helpers.LocationSpinnerItem;
import com.example.assetmanagementsystem.assetdb.model.Asset;
import com.example.assetmanagementsystem.glide.GlideApp;
import com.example.assetmanagementsystem.util.CameraUtil;
import com.example.assetmanagementsystem.util.Constants;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseApp;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class AddAssetFragment extends Fragment {
    protected AssetDatabase assetDatabase;
    protected List<LocationSpinnerItem> locationItems;
    protected List<EmployeeSpinnerItem> employeeItems;
    protected ArrayAdapter<LocationSpinnerItem> locationsAdapter;
    protected ArrayAdapter<EmployeeSpinnerItem> employeeAdapter;
    private Spinner spinnerCategory;
    protected Spinner spinnerLocation;
    protected Spinner spinnerEmployee;
    private Button buttonScan;
    private Button buttonAdd;
    private Button buttonGallery;
    private Button buttonCamera;
    private TextInputEditText editTextBarcode;
    private TextInputEditText editTextName;
    private TextInputEditText editTextDescription;
    private TextInputEditText editTextPrice;
    private String selectedCategory;
    private LocationSpinnerItem selectedLocationItem;
    private EmployeeSpinnerItem selectedEmployeeItem;
    protected Asset asset;
    protected Uri photoUri;
    protected String imageUrl;
    private ImageView imageView;
    protected boolean imageChanged = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_add_asset, container, false);

        spinnerCategory = rootView.findViewById(R.id.spinner_category);
        spinnerLocation = rootView.findViewById(R.id.spinner_location);
        spinnerEmployee = rootView.findViewById(R.id.spinner_employee);
        buttonAdd = rootView.findViewById(R.id.button_add);

        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCategory = (String) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

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

        ArrayList<String> categories = Arrays.stream(AssetCategoryEnum.values())
                .map(AssetCategoryEnum::getDisplayName)
                .collect(Collectors.toCollection(ArrayList::new));

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                R.layout.item_spinner_selected, categories);
        adapter.setDropDownViewResource(R.layout.item_spinner_selected);
        spinnerCategory.setAdapter(adapter);


        locationItems = new ArrayList<>();
        displayLocations();

        employeeItems = new ArrayList<>();
        displayEmployees();

        editTextName = rootView.findViewById(R.id.et_assetName);
        editTextDescription = rootView.findViewById(R.id.et_description);
        editTextPrice = rootView.findViewById(R.id.et_price);
        imageView = rootView.findViewById(R.id.image);

        Bundle bundle = getArguments();
        if (bundle != null) {
            asset = bundle.getParcelable("asset");
            if (asset != null) {
                editTextName.setText(asset.getName());
                editTextDescription.setText(asset.getDescription());
                editTextPrice.setText(String.valueOf(asset.getPrice()));
                LinearLayout layoutImageBarcode = rootView.findViewById(R.id.layout_imageBarcode);
                layoutImageBarcode.setVisibility(View.VISIBLE);
                LinearLayout layoutBarcode = rootView.findViewById(R.id.layout_barcode);
                layoutBarcode.setVisibility(View.GONE);
                TextView twBarcode = rootView.findViewById(R.id.tw_barcode);
                twBarcode.setText(getString(R.string.input_barcode) + ": " + asset.getBarcode());
                TextView twDate = rootView.findViewById(R.id.tw_date);
                twDate.setText(getString(R.string.date) + ": " + asset.getDateCreated());
                buttonAdd.setText(getString(R.string.update_asset));

                loadImage(asset.getImageUrl());
                int defaultPositionCategory = adapter.getPosition(asset.getCategory().getDisplayName());
                if (defaultPositionCategory != -1) {
                    spinnerCategory.setSelection(defaultPositionCategory);
                }
                buttonAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!editTextName.getText().toString().isEmpty() && !editTextDescription.getText().toString().isEmpty()
                                && !editTextPrice.getText().toString().isEmpty() && !selectedCategory.isEmpty()
                                && selectedLocationItem != null && selectedEmployeeItem != null) {
                            asset.setName(editTextName.getText().toString());
                            asset.setDescription(editTextDescription.getText().toString());
                            asset.setPrice(Double.parseDouble(editTextPrice.getText().toString()));
                            asset.setCategory(AssetCategoryEnum.fromString(selectedCategory));
                            asset.setEmployeeId(selectedEmployeeItem.getEmployeeId());
                            asset.setLocationId(selectedLocationItem.getLocationId());
                            if (imageChanged) {
                                imageUrl = "images/" + System.currentTimeMillis() + ".jpg";
                                asset.setImageUrl(imageUrl);
                            }

                            new AssetsAsync.UpdateTask(AddAssetFragment.this, asset).execute();
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

            buttonAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!editTextBarcode.getText().toString().isEmpty() && !editTextName.getText().toString().isEmpty() && !editTextDescription.getText().toString().isEmpty()
                            && !editTextPrice.getText().toString().isEmpty() && !selectedCategory.isEmpty()
                            && selectedLocationItem != null && selectedEmployeeItem != null) {
                        imageUrl = photoUri != null ? "images/" + System.currentTimeMillis() + ".jpg" : "no";
                        asset = new Asset(Long.parseLong(editTextBarcode.getText().toString()), editTextName.getText().toString(),
                                editTextDescription.getText().toString(), Double.parseDouble(editTextPrice.getText().toString()),
                                AssetCategoryEnum.fromString(selectedCategory), selectedEmployeeItem.getEmployeeId(),
                                selectedLocationItem.getLocationId(), imageUrl);

                        new AssetsAsync.InsertTask(AddAssetFragment.this, asset).execute();
                    } else
                        Toast.makeText(requireContext(), getString(R.string.missing_fields), Toast.LENGTH_SHORT).show();
                }
            });
        }

        FirebaseApp.initializeApp(requireContext());

        buttonCamera = rootView.findViewById(R.id.button_camera);
        buttonCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    handleCamera();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        buttonGallery = rootView.findViewById(R.id.button_gallery);
        buttonGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleGallery();
            }
        });

        return rootView;
    }

    private void loadImage(String imageUrl) {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(imageUrl);
        RequestOptions requestOptions = new RequestOptions()
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder);

        GlideApp.with(this)
                .load(storageReference)
                .apply(requestOptions)
                .into(imageView);
    }

    private void handleScanning() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, Constants.REQUEST_CODE_PERMISSION_QR);
            return;
        }
        CameraUtil.startScanning(this);
    }
    private void handleCamera() throws IOException {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, Constants.REQUEST_CODE_PERMISSION_CAMERA);
            return;
        }
        photoUri = CameraUtil.takePhoto(this);
    }
    private void handleGallery() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_MEDIA_IMAGES}, Constants.REQUEST_CODE_PERMISSION_READ_MEDIA_IMAGES);
            return;
        }
        CameraUtil.openGallery(this);
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
        } else if (requestCode == Constants.REQUEST_CODE_PERMISSION_CAMERA) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                try {
                    photoUri = CameraUtil.takePhoto(this);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else {
                Toast.makeText(requireContext(), getString(R.string.camera_denied), Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == Constants.REQUEST_CODE_PERMISSION_READ_MEDIA_IMAGES) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                CameraUtil.openGallery(this);
            } else {
                Toast.makeText(requireContext(), getString(R.string.images_denied), Toast.LENGTH_SHORT).show();
            }
        }
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.REQUEST_CODE_IMAGE_PICK && resultCode == Activity.RESULT_OK) {
            if (data != null && data.getData() != null) {
                photoUri = data.getData();
                Toast.makeText(getContext(), getString(R.string.photo_selected), Toast.LENGTH_SHORT).show();
            } else if (photoUri != null) {
                Toast.makeText(getContext(), getString(R.string.photo_captured), Toast.LENGTH_SHORT).show();
            }
            if (asset != null) {
                imageView.setImageURI(photoUri);
                imageChanged = true;
            }

        } else {
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


    private void displayLocations() {
        assetDatabase = AssetDatabase.getInstance(requireContext());
        new AssetsAsync.RetrieveLocationsTask(this).execute();
    }

    private void displayEmployees() {
        assetDatabase = AssetDatabase.getInstance(requireContext());
        new AssetsAsync.RetrieveEmployeesTask(this).execute();
    }

}