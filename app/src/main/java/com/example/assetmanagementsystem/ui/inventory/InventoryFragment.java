package com.example.assetmanagementsystem.ui.inventory;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.assetmanagementsystem.R;
import com.example.assetmanagementsystem.adapter.EmployeesAdapter;
import com.example.assetmanagementsystem.adapter.InventoryAdapter;
import com.example.assetmanagementsystem.assetdb.AssetDatabase;
import com.example.assetmanagementsystem.assetdb.helpers.InventoryDetails;
import com.example.assetmanagementsystem.assetdb.model.Inventory;
import com.example.assetmanagementsystem.databinding.FragmentInventoryBinding;
import com.example.assetmanagementsystem.ui.employees.EmployeesAsync;

import java.util.ArrayList;
import java.util.List;

public class InventoryFragment extends Fragment implements InventoryAdapter.OnInventoryItemClick {

    private FragmentInventoryBinding binding;
    private RecyclerView recyclerView;
    protected AssetDatabase assetDatabase;
    protected List<InventoryDetails> inventoryDetails;
    protected InventoryAdapter inventoryAdapter;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentInventoryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        binding.fabAddInventory.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_main);
            navController.navigate(R.id.action_nav_inventory_to_nav_add_inventory);
        });

        recyclerView = root.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        inventoryDetails = new ArrayList<>();

        inventoryAdapter = new InventoryAdapter(inventoryDetails, requireContext(), this);
        recyclerView.setAdapter(inventoryAdapter);

        displayList();

        return root;
    }

    private void displayList() {
        assetDatabase = AssetDatabase.getInstance(requireContext());
        new InventoryAsync.RetrieveTask(this).execute();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onInventoryClick(int pos) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Do you want to update inventory?")
                .setItems(new String[]{"Yes", "No"}, (dialogInterface, which) -> {
                    switch (which) {
                        case 0:
                            Bundle bundle = new Bundle();
                            bundle.putParcelable("inventory", inventoryDetails.get(pos).getInventory());
                            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_main);
                            navController.navigate(R.id.action_nav_inventory_to_nav_add_inventory, bundle);
                            break;

                        case 1:
                            break;
                    }
                }).show();
    }

    @Override
    public void deleteInventory(int pos) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Do you want to delete inventory?")
                .setItems(new String[]{"Yes", "No"}, (dialogInterface, which) -> {
                    switch (which) {
                        case 0:
                            new InventoryAsync.DeleteTask(this, pos).execute();
                            break;
                        case 1:
                            break;
                    }
                }).show();
    }
}