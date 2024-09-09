package com.example.assetmanagementsystem.assetdb.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.assetmanagementsystem.assetdb.model.Employee;
import com.example.assetmanagementsystem.util.Constants;

import java.util.List;

@Dao
public interface EmployeeDao {
    @Query("SELECT * FROM " + Constants.TABLE_NAME_EMPLOYEE)
    List<Employee> getEmployees();

    @Insert
    long insertEmployee(Employee employee);

    @Update
    void updateEmployee(Employee employee);

    @Delete
    void deleteEmployee(Employee employee);
    @Delete
    void deleteEmployees(Employee... employees);

}
