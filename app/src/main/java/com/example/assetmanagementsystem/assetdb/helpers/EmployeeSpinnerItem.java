package com.example.assetmanagementsystem.assetdb.helpers;

public class EmployeeSpinnerItem {
    private long employeeId;
    private String name;

    public EmployeeSpinnerItem(long employeeId, String name) {
        this.employeeId = employeeId;
        this.name = name;
    }

    public long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(long employeeId) {
        this.employeeId = employeeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
