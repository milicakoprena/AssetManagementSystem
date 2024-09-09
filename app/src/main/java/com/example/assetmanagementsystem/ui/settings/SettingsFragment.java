package com.example.assetmanagementsystem.ui.settings;

import static android.content.Context.MODE_PRIVATE;


import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.example.assetmanagementsystem.R;
import com.example.assetmanagementsystem.databinding.FragmentSettingsBinding;

import java.util.Locale;

public class SettingsFragment extends Fragment {

    private FragmentSettingsBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        String[] languages = {
                getString(R.string.english),
                getString(R.string.serbian)
        };
        final String[] languageCodes = {"en", "sr"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), R.layout.item_spinner_selected, languages);
        adapter.setDropDownViewResource(R.layout.item_spinner_selected);
        binding.spinnerLanguage.setAdapter(adapter);

        SharedPreferences preferences = requireActivity().getSharedPreferences("Settings", MODE_PRIVATE);
        String savedLanguageCode = preferences.getString("My_Lang", "en");
        if (savedLanguageCode.equals("sr")) {
            binding.spinnerLanguage.setSelection(1);
        } else {
            binding.spinnerLanguage.setSelection(0);
        }

        binding.spinnerLanguage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedLanguageCode = languageCodes[position];
                if (!savedLanguageCode.equals(selectedLanguageCode)) {
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("My_Lang", selectedLanguageCode);
                    editor.apply();

                    setLocale(selectedLanguageCode);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        return root;
    }

    private void setLocale(String langCode) {
        Locale locale = new Locale(langCode);
        Locale.setDefault(locale);
        android.content.res.Configuration config = new android.content.res.Configuration();
        config.locale = locale;
        requireActivity().getResources().updateConfiguration(config, requireActivity().getResources().getDisplayMetrics());

        getActivity().recreate();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}