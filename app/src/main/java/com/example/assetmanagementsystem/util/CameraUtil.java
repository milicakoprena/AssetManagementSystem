package com.example.assetmanagementsystem.util;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.example.assetmanagementsystem.R;
import com.google.zxing.integration.android.IntentIntegrator;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CameraUtil {

    public static void startScanning(Fragment fragment) {
        IntentIntegrator intentIntegrator = IntentIntegrator.forSupportFragment(fragment);
        intentIntegrator.setPrompt(fragment.getString(R.string.scan_barcode));
        intentIntegrator.setOrientationLocked(true);
        intentIntegrator.initiateScan();
    }



    public static Uri takePhoto(Fragment fragment) throws IOException {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(fragment.requireActivity().getPackageManager()) != null) {
            File photoFile = createImageFile(fragment);
            if (photoFile != null) {
                Uri photoUri = FileProvider.getUriForFile(fragment.requireContext(),
                        "com.example.assetmanagementsystem.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                fragment.startActivityForResult(takePictureIntent, Constants.REQUEST_CODE_IMAGE_PICK);
                return photoUri;
            }
        }
        return null;
    }

    public static File createImageFile(Fragment fragment) throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";

        File storageDir = fragment.requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        File imageFile = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );

        return imageFile;
    }


    public static void openGallery(Fragment fragment) {
        Intent pickPhotoIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        fragment.startActivityForResult(pickPhotoIntent, Constants.REQUEST_CODE_IMAGE_PICK);
    }
}
