package com.example.assetmanagementsystem.ui.employees;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.assetmanagementsystem.R;
import com.example.assetmanagementsystem.assetdb.AssetDatabase;
import com.example.assetmanagementsystem.assetdb.model.Employee;
import com.google.android.material.textfield.TextInputEditText;


public class AddEmployeeFragment extends Fragment {
    private TextInputEditText et_firstName, et_lastName, et_email;
    protected AssetDatabase assetDatabase;
    protected Employee employee;

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
                button_add.setText(getString(R.string.update_employee));
                button_add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!et_firstName.getText().toString().isEmpty() && !et_lastName.getText().toString().isEmpty() &&
                                !et_email.getText().toString().isEmpty()) {
                            employee.setFirstName(et_firstName.getText().toString());
                            employee.setLastName(et_lastName.getText().toString());
                            employee.setEmail(et_email.getText().toString());
                            new EmployeesAsync.UpdateTask(AddEmployeeFragment.this, employee).execute();
                        } else
                            Toast.makeText(requireContext(), getString(R.string.all_fields), Toast.LENGTH_SHORT).show();
                    }
                });
            }

        } else {
            button_add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!et_firstName.getText().toString().isEmpty() && !et_lastName.getText().toString().isEmpty() &&
                            !et_email.getText().toString().isEmpty()) {
                        employee = new Employee(et_firstName.getText().toString(), et_lastName.getText().toString(), et_email.getText().toString());
                        new EmployeesAsync.InsertTask(AddEmployeeFragment.this, employee).execute();
                    } else
                        Toast.makeText(requireContext(), getString(R.string.all_fields), Toast.LENGTH_SHORT).show();
                }
            });
        }
        return rootView;
    }

}