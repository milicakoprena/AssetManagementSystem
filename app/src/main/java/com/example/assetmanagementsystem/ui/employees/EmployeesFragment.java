package com.example.assetmanagementsystem.ui.employees;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.assetmanagementsystem.R;
import com.example.assetmanagementsystem.databinding.FragmentEmployeesBinding;

public class EmployeesFragment extends Fragment {

    private FragmentEmployeesBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        EmployeesViewModel employeesViewModel =
                new ViewModelProvider(this).get(EmployeesViewModel.class);

        binding = FragmentEmployeesBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textEmployees;
        employeesViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        binding.fabAddEmployee.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_main);
            navController.navigate(R.id.action_nav_employees_to_nav_add_employee);
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}