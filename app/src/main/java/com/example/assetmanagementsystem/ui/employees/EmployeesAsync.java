package com.example.assetmanagementsystem.ui.employees;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.assetmanagementsystem.R;
import com.example.assetmanagementsystem.assetdb.model.Employee;

import java.lang.ref.WeakReference;
import java.util.List;

public class EmployeesAsync {
    protected static class RetrieveTask extends AsyncTask<Void, Void, List<Employee>> {
        private WeakReference<EmployeesFragment> reference;
        RetrieveTask(EmployeesFragment fragment) {
            reference = new WeakReference<>(fragment);
        }

        @Override
        protected List<Employee> doInBackground(Void... voids) {
            if (reference.get() != null)
                return reference.get().assetDatabase.getEmployeeDao().getEmployees();
            else
                return null;
        }

        @Override
        protected void onPostExecute(List<Employee> employees) {
            EmployeesFragment fragment = reference.get();
            if (fragment != null && employees != null) {
                fragment.employees.clear();
                fragment.employees.addAll(employees);
                fragment.filteredEmployees.clear();
                fragment.filteredEmployees.addAll(employees);
                fragment.employeesAdapter.notifyDataSetChanged();
            }
        }
    }

    protected static class DeleteTask extends AsyncTask<Void, Void, Void> {
        private WeakReference<EmployeesFragment> reference;
        private int pos;

        public DeleteTask(EmployeesFragment fragment, int pos) {
            reference = new WeakReference<>(fragment);
            this.pos = pos;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            reference.get().assetDatabase.getEmployeeDao().deleteEmployee(reference.get().employees.get(pos));
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            reference.get().employees.remove(pos);
            reference.get().employeesAdapter.notifyItemRemoved(pos);
            Toast.makeText(reference.get().requireContext(), reference.get().getString(R.string.employee_deleted), Toast.LENGTH_SHORT).show();
        }
    }

    protected static class InsertTask extends AsyncTask<Void, Void, Boolean> {
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
                Toast.makeText(fragment.requireContext(), fragment.getString(R.string.employee_added), Toast.LENGTH_SHORT).show();
                NavController navController = Navigation.findNavController(fragment.requireActivity(), R.id.nav_host_fragment_content_main);
                navController.navigate(R.id.action_nav_add_employee_to_nav_employees);
            }
        }
    }

    protected static class UpdateTask extends AsyncTask<Void, Void, Boolean> {
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
                Toast.makeText(fragment.requireContext(), fragment.getString(R.string.employee_updated), Toast.LENGTH_SHORT).show();
                NavController navController = Navigation.findNavController(fragment.requireActivity(), R.id.nav_host_fragment_content_main);
                navController.navigate(R.id.action_nav_add_employee_to_nav_employees);
            }
        }
    }
}
