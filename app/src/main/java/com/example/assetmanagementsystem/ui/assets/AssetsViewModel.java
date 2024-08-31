package com.example.assetmanagementsystem.ui.assets;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AssetsViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public AssetsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is assets fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}