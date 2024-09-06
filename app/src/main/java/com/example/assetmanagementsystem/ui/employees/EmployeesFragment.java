package com.example.assetmanagementsystem.ui.employees;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.assetmanagementsystem.R;
import com.example.assetmanagementsystem.adapter.EmployeesAdapter;
import com.example.assetmanagementsystem.assetdb.AssetDatabase;
import com.example.assetmanagementsystem.assetdb.model.Employee;
import com.example.assetmanagementsystem.databinding.FragmentEmployeesBinding;

import java.util.ArrayList;
import java.util.List;

public class EmployeesFragment extends Fragment implements EmployeesAdapter.OnEmployeeItemClick {

    private FragmentEmployeesBinding binding;
    private RecyclerView recyclerView;
    protected AssetDatabase assetDatabase;
    protected List<Employee> employees;
    protected EmployeesAdapter employeesAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentEmployeesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.fabAddEmployee.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_main);
            navController.navigate(R.id.action_nav_employees_to_nav_add_employee);
        });


        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        employees = new ArrayList<>();

        employeesAdapter = new EmployeesAdapter(employees, requireContext(), this);
        recyclerView.setAdapter(employeesAdapter);

        displayList();
    }

    private void displayList() {
        assetDatabase = AssetDatabase.getInstance(requireContext());
        new EmployeesAsync.RetrieveTask(this).execute();
    }

    @Override
    public void onEmployeeClick(int pos) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Do you want to update employee " + employees.get(pos).getFirstName() + "?")
                .setItems(new String[]{"Yes", "No"}, (dialogInterface, which) -> {
                    switch (which) {
                        case 0:
                            Bundle bundle = new Bundle();
                            bundle.putParcelable("employee", employees.get(pos));
                            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_main);
                            navController.navigate(R.id.action_nav_employees_to_nav_add_employee, bundle);
                            break;

                        case 1:
                            break;
                    }
                }).show();
    }

    @Override
    public void deleteEmployee(int pos) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Do you want to delete employee " + employees.get(pos).getFirstName() + "?")
                .setItems(new String[]{"Yes", "No"}, (dialogInterface, which) -> {
                    switch (which) {
                        case 0:
                            new EmployeesAsync.DeleteTask(this, pos).execute();
                            break;
                        case 1:
                            break;
                    }
                }).show();

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
