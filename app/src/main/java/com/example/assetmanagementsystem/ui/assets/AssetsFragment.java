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

public class AssetsFragment extends Fragment implements AssetsAdapter.OnAssetItemClick{

    private FragmentAssetsBinding binding;
    private RecyclerView recyclerView;
    private AssetDatabase assetDatabase;
    private List<Asset> assets;
    private AssetsAdapter adapter;

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

        adapter = new AssetsAdapter(assets, requireContext(), this);
        recyclerView.setAdapter(adapter);

        displayList();
    }

    private void displayList() {
        assetDatabase = AssetDatabase.getInstance(requireContext());
        new AssetsFragment.RetrieveTask(this).execute();
    }

    private static class RetrieveTask extends AsyncTask<Void, Void, List<Asset>> {
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
                fragment.adapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onAssetClick(int pos) {

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
