package com.example.assetmanagementsystem.ui.assets.addasset;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.assetmanagementsystem.R;
import com.example.assetmanagementsystem.assetdb.AssetDatabase;
import com.example.assetmanagementsystem.assetdb.enums.AssetCategoryEnum;
import com.example.assetmanagementsystem.assetdb.helpers.EmployeeSpinnerItem;
import com.example.assetmanagementsystem.assetdb.helpers.LocationSpinnerItem;
import com.example.assetmanagementsystem.assetdb.model.Asset;
import com.example.assetmanagementsystem.assetdb.model.Employee;
import com.example.assetmanagementsystem.assetdb.model.Location;
import com.example.assetmanagementsystem.ui.employees.addemployee.AddEmployeeFragment;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseApp;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;


import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class AddAssetFragment extends Fragment {
    private AssetDatabase assetDatabase;
    private List<LocationSpinnerItem> locationItems;
    private List<EmployeeSpinnerItem> employeeItems;
    private ArrayAdapter<LocationSpinnerItem> locationsAdapter;
    private ArrayAdapter<EmployeeSpinnerItem> employeeAdapter;
    private Spinner spinnerCategory;
    private Spinner spinnerLocation;
    private Spinner spinnerEmployee;
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
    private Asset asset;
    private Uri photoUri;
    private String imageUrl;

    private static final int REQUEST_CODE_PERMISSION_CAMERA = 1001;
    private static final int REQUEST_CODE_PERMISSION_QR = 1002;
    private static final int REQUEST_CODE_PERMISSION_READ_MEDIA_IMAGES = 1003;
    private static final int REQUEST_CODE_IMAGE_PICK = 1004;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_add_asset, container, false);

        spinnerCategory = rootView.findViewById(R.id.spinner_category);
        spinnerLocation = rootView.findViewById(R.id.spinner_location);
        spinnerEmployee = rootView.findViewById(R.id.spinner_employee);

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

        buttonScan = rootView.findViewById(R.id.button_scan);
        editTextBarcode = rootView.findViewById(R.id.et_barcode);
        editTextName = rootView.findViewById(R.id.et_assetName);
        editTextDescription = rootView.findViewById(R.id.et_description);
        editTextPrice = rootView.findViewById(R.id.et_price);
        buttonScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleScanning();
            }
        });

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

        buttonAdd = rootView.findViewById(R.id.button_add);
        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageUrl = "images/" + System.currentTimeMillis() + ".jpg";
                asset = new Asset(Long.parseLong(editTextBarcode.getText().toString()), editTextName.getText().toString(),
                        editTextDescription.getText().toString(), Double.parseDouble(editTextPrice.getText().toString()),
                        AssetCategoryEnum.fromString(selectedCategory), selectedEmployeeItem.getEmployeeId(),
                        selectedLocationItem.getLocationId(), imageUrl);

                new AddAssetFragment.InsertTask(AddAssetFragment.this, asset).execute();
            }
        });
        return rootView;
    }

    private void handleScanning() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CODE_PERMISSION_QR);
            return;
        }
        startScanning();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSION_QR) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startScanning();
            } else {
                Toast.makeText(requireContext(), "Camera permission denied", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == REQUEST_CODE_PERMISSION_CAMERA) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                try {
                    takePhoto();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else {
                Toast.makeText(requireContext(), "Camera permission denied", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == REQUEST_CODE_PERMISSION_READ_MEDIA_IMAGES) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            } else {
                Toast.makeText(requireContext(), "Permission denied to read images", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void startScanning() {
        IntentIntegrator intentIntegrator = IntentIntegrator.forSupportFragment(this);
        intentIntegrator.setPrompt("Scan a barcode or QR Code");
        intentIntegrator.setOrientationLocked(true);
        intentIntegrator.initiateScan();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_IMAGE_PICK && resultCode == Activity.RESULT_OK) {
            if (data != null && data.getData() != null) {
                photoUri = data.getData();
                Toast.makeText(getContext(), "Photo selected from gallery!", Toast.LENGTH_SHORT).show();
            } else if (photoUri != null) {
                Toast.makeText(getContext(), "Photo captured from camera!", Toast.LENGTH_SHORT).show();
            }
        }
        else {
            IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            if (intentResult != null) {
                if (intentResult.getContents() == null) {
                    Toast.makeText(getContext(), "Cancelled!", Toast.LENGTH_SHORT).show();
                } else {
                    editTextBarcode.setText(intentResult.getContents());
                }
            }
        }
    }

    private void handleCamera() throws IOException {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CODE_PERMISSION_CAMERA);
            return;
        }
        takePhoto();
    }

    private void handleGallery() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_MEDIA_IMAGES}, REQUEST_CODE_PERMISSION_READ_MEDIA_IMAGES);
            return;
        }
        openGallery();
    }

    private void openGallery() {
        Intent pickPhotoIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhotoIntent, REQUEST_CODE_IMAGE_PICK);
    }


    private void takePhoto() throws IOException {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
            File photoFile = createImageFile();
            if (photoFile != null) {
                photoUri = FileProvider.getUriForFile(requireContext(),
                        "com.example.assetmanagementsystem.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(takePictureIntent, REQUEST_CODE_IMAGE_PICK);
            }
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";

        File storageDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        File imageFile = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );

        return imageFile;
    }


    private void uploadImageToFirebase(Uri fileUri, String imageUrl) {
        if (fileUri != null) {
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();

            StorageReference imageRef = storageRef.child(imageUrl);

            imageRef.putFile(fileUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        Toast.makeText(requireContext(), "Upload successful!", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(requireContext(), "Upload failed!", Toast.LENGTH_SHORT).show();
                    });
        }
    }




    private void displayLocations() {
        assetDatabase = AssetDatabase.getInstance(requireContext());
        new AddAssetFragment.RetrieveLocationsTask(this).execute();
    }

    private void displayEmployees() {
        assetDatabase = AssetDatabase.getInstance(requireContext());
        new AddAssetFragment.RetrieveEmployeesTask(this).execute();
    }

    private static class RetrieveLocationsTask extends AsyncTask<Void, Void, List<Location>> {
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
            }
        }
    }

    private static class RetrieveEmployeesTask extends AsyncTask<Void, Void, List<Employee>> {
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
            }
        }
    }

    private static class InsertTask extends AsyncTask<Void, Void, Boolean> {
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
                long j = fragment.assetDatabase.getAssetDao().insertAsset(asset);
                fragment.uploadImageToFirebase(fragment.photoUri, fragment.imageUrl);
                Log.e("ID ", "doInBackground: " + j);
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean bool) {
            AddAssetFragment fragment = fragmentReference.get();
            if (fragment != null && bool) {
                Toast.makeText(fragment.requireContext(), "Asset added successfully", Toast.LENGTH_SHORT).show();
                NavController navController = Navigation.findNavController(fragment.requireActivity(), R.id.nav_host_fragment_content_main);
                navController.navigate(R.id.action_nav_add_asset_to_nav_assets);
            }
        }
    }

}