package com.example.assetmanagementsystem.ui.employees.addemployee;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.assetmanagementsystem.R;
import com.example.assetmanagementsystem.assetdb.AssetDatabase;
import com.example.assetmanagementsystem.assetdb.model.Employee;
import com.google.android.material.textfield.TextInputEditText;

import java.lang.ref.WeakReference;


public class AddEmployeeFragment extends Fragment {
    private TextInputEditText et_firstName, et_lastName, et_email;
    private AssetDatabase assetDatabase;
    private Employee employee;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_add_employee, container, false);

        et_firstName = rootView.findViewById(R.id.et_firstName);
        et_lastName = rootView.findViewById(R.id.et_lastName);
        et_email = rootView.findViewById(R.id.et_email);
        assetDatabase = AssetDatabase.getInstance(requireContext());
        Button button_add = rootView.findViewById(R.id.button_add);

        Bundle bundle = getArguments();
        if (bundle != null) {
            employee = bundle.getParcelable("employee");
            if (employee != null) {
                et_firstName.setText(employee.getFirstName());
                et_lastName.setText(employee.getLastName());
                et_email.setText(employee.getEmail());
                button_add.setText("UPDATE EMPLOYEE");
                button_add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        employee.setFirstName(et_firstName.getText().toString());
                        employee.setLastName(et_lastName.getText().toString());
                        employee.setEmail(et_email.getText().toString());
                        new UpdateTask(AddEmployeeFragment.this, employee).execute();
                    }
                });
            }

        } else {
            button_add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    employee = new Employee(et_firstName.getText().toString(), et_lastName.getText().toString(), et_email.getText().toString());
                    new InsertTask(AddEmployeeFragment.this, employee).execute();
                }
            });
        }
        return rootView;
    }


    private static class InsertTask extends AsyncTask<Void, Void, Boolean> {
        private WeakReference<AddEmployeeFragment> fragmentReference;
        private Employee employee;

        InsertTask(AddEmployeeFragment fragment, Employee employee) {
            fragmentReference = new WeakReference<>(fragment);
            this.employee = employee;
        }

        @Override
        protected Boolean doInBackground(Void... objs) {
            AddEmployeeFragment fragment = fragmentReference.get();
            if (fragment != null) {
                long j = fragment.assetDatabase.getEmployeeDao().insertEmployee(employee);
                employee.setEmployeeId(j);
                Log.e("ID ", "doInBackground: " + j);
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean bool) {
            AddEmployeeFragment fragment = fragmentReference.get();
            if (fragment != null && bool) {
                Toast.makeText(fragment.requireContext(), "Employee added successfully", Toast.LENGTH_SHORT).show();
                NavController navController = Navigation.findNavController(fragment.requireActivity(), R.id.nav_host_fragment_content_main);
                navController.navigate(R.id.action_nav_add_employee_to_nav_employees);
            }
        }
    }

    private static class UpdateTask extends AsyncTask<Void, Void, Boolean> {
        private WeakReference<AddEmployeeFragment> fragmentReference;
        private Employee employee;

        UpdateTask(AddEmployeeFragment fragment, Employee employee) {
            fragmentReference = new WeakReference<>(fragment);
            this.employee = employee;
        }

        @Override
        protected Boolean doInBackground(Void... objs) {
            AddEmployeeFragment fragment = fragmentReference.get();
            if (fragment != null) {
                fragment.assetDatabase.getEmployeeDao().updateEmployee(employee);
                Log.e("ID ", "employee updated");
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean bool) {
            AddEmployeeFragment fragment = fragmentReference.get();
            if (fragment != null && bool) {
                Toast.makeText(fragment.requireContext(), "Employee updated successfully", Toast.LENGTH_SHORT).show();
                NavController navController = Navigation.findNavController(fragment.requireActivity(), R.id.nav_host_fragment_content_main);
                navController.navigate(R.id.action_nav_add_employee_to_nav_employees);
            }
        }
    }
}