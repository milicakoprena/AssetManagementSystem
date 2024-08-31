package com.example.assetmanagementsystem.ui.assets;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.assetmanagementsystem.assetdb.AssetDatabase;
import com.example.assetmanagementsystem.assetdb.dao.AssetDao;
import com.example.assetmanagementsystem.assetdb.model.Asset;
import com.example.assetmanagementsystem.databinding.FragmentAssetsBinding;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AssetsFragment extends Fragment {

    private FragmentAssetsBinding binding;
    private AssetDatabase assetDatabase;
    private AssetDao assetDao;
    private ExecutorService executorService;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentAssetsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        assetDatabase = AssetDatabase.getInstance(requireContext());
        assetDao = assetDatabase.getAssetDao();
        executorService = Executors.newSingleThreadExecutor();

        executorService.execute(() -> {
            List<Asset> assets = assetDao.getAssets();

            requireActivity().runOnUiThread(() -> {
                StringBuilder assetsText = new StringBuilder();
                for (Asset asset : assets) {
                    assetsText.append(asset.toString()).append("\n");
                }

                if (binding != null) {
                    binding.textAssets.setText(assetsText.toString());
                }
            });
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        executorService.shutdown();
    }
}
