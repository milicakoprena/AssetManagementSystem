package com.example.assetmanagementsystem.ui.inventory;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.assetmanagementsystem.R;
import com.example.assetmanagementsystem.databinding.FragmentEmployeesBinding;
import com.example.assetmanagementsystem.databinding.FragmentInventoryBinding;
import com.example.assetmanagementsystem.databinding.FragmentLocationsBinding;
import com.example.assetmanagementsystem.ui.employees.EmployeesViewModel;
import com.example.assetmanagementsystem.ui.locations.LocationsViewModel;

public class InventoryFragment extends Fragment {

    private FragmentInventoryBinding binding;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        InventoryViewModel inventoryViewModel =
                new ViewModelProvider(this).get(InventoryViewModel.class);

        binding = FragmentInventoryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textInventory;
        inventoryViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}