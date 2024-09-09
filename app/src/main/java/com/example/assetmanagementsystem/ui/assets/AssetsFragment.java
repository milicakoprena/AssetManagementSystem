package com.example.assetmanagementsystem.ui.assets;

import android.app.AlertDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.assetmanagementsystem.R;
import com.example.assetmanagementsystem.adapter.AssetsAdapter;
import com.example.assetmanagementsystem.adapter.EmployeesAdapter;
import com.example.assetmanagementsystem.assetdb.AssetDatabase;
import com.example.assetmanagementsystem.assetdb.dao.AssetDao;
import com.example.assetmanagementsystem.assetdb.model.Asset;
import com.example.assetmanagementsystem.assetdb.model.Employee;
import com.example.assetmanagementsystem.databinding.FragmentAssetsBinding;
import com.example.assetmanagementsystem.ui.employees.EmployeesFragment;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class AssetsFragment extends Fragment implements AssetsAdapter.OnAssetItemClick{

    private FragmentAssetsBinding binding;
    private RecyclerView recyclerView;
    protected AssetDatabase assetDatabase;
    protected List<Asset> assets;
    protected List<Asset> filteredAssets;
    protected AssetsAdapter adapter;
    private SearchView searchAssetName;
    private SearchView searchAssetDesc;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentAssetsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.fabAddAsset.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_main);
            navController.navigate(R.id.action_nav_assets_to_nav_add_asset);
        });

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        assets = new ArrayList<>();
        filteredAssets = new ArrayList<>();

        adapter = new AssetsAdapter(filteredAssets, requireContext(), this);
        recyclerView.setAdapter(adapter);

        searchAssetName = view.findViewById(R.id.search_assetName);
        searchAssetName.setIconifiedByDefault(false);
        searchAssetName.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
               searchByName(query);

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchByName(newText);

                return false;
            }
        });

        searchAssetDesc = view.findViewById(R.id.search_assetDesc);
        searchAssetDesc.setIconifiedByDefault(false);
        searchAssetDesc.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchByDesc(query);

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchByDesc(newText);

                return false;
            }
        });

        displayList();
    }

    private void displayList() {
        assetDatabase = AssetDatabase.getInstance(requireContext());
        new AssetsAsync.RetrieveTask(this).execute();
    }

    private void searchByName(String query){
        filteredAssets = assets.stream()
                .filter(asset -> asset.getName().toLowerCase().contains(query.toLowerCase()))
                .collect(Collectors.toList());

        adapter.updateData(filteredAssets);
    }

    private void searchByDesc(String query){
        filteredAssets = assets.stream()
                .filter(asset -> asset.getDescription().toLowerCase().contains(query.toLowerCase()))
                .collect(Collectors.toList());

        adapter.updateData(filteredAssets);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onAssetClick(int pos) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Do you want to update asset " + assets.get(pos).getName() + "?")
                .setItems(new String[]{"Yes", "No"}, (dialogInterface, which) -> {
                    switch (which) {
                        case 0:
                            Bundle bundle = new Bundle();
                            bundle.putParcelable("asset", assets.get(pos));
                            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_main);
                            navController.navigate(R.id.action_nav_assets_to_nav_add_asset, bundle);
                            break;

                        case 1:
                            break;
                    }
                }).show();
    }

    @Override
    public void deleteAsset(int pos) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Do you want to delete asset " + assets.get(pos).getName() + "?")
                .setItems(new String[]{"Yes", "No"}, (dialogInterface, which) -> {
                    switch (which) {
                        case 0:
                            new AsyncTask<Void, Void, Void>() {
                                @Override
                                protected Void doInBackground(Void... voids) {
                                    assetDatabase.getAssetDao().deleteAsset(assets.get(pos));
                                    return null;
                                }

                                @Override
                                protected void onPostExecute(Void aVoid) {
                                    assets.remove(pos);
                                    adapter.notifyItemRemoved(pos);
                                    Toast.makeText(requireContext(), "Asset deleted", Toast.LENGTH_SHORT).show();
                                }
                            }.execute();
                            break;
                        case 1:
                            break;
                    }
                }).show();

    }


}
