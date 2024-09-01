package com.example.assetmanagementsystem.assetdb.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.example.assetmanagementsystem.util.Constants;

import java.io.Serializable;
import java.util.Objects;

@Entity(tableName = Constants.TABLE_NAME_EMPLOYEE)
public class Employee implements Serializable, Parcelable {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "employee_id")
    private long employeeId;
    @ColumnInfo(name = "first_name")
    private String firstName;
    @ColumnInfo(name = "last_name")
    private String lastName;
    private String email;

    public Employee(String firstName, String lastName, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    @Ignore
    public Employee() {
    }

    protected Employee(Parcel in) {
        employeeId = in.readLong();
        firstName = in.readString();
        lastName = in.readString();
        email = in.readString();
    }

    public static final Creator<Employee> CREATOR = new Creator<Employee>() {
        @Override
        public Employee createFromParcel(Parcel in) {
            return new Employee(in);
        }

        @Override
        public Employee[] newArray(int size) {
            return new Employee[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(employeeId);
        dest.writeString(firstName);
        dest.writeString(lastName);
        dest.writeString(email);
    }

    public long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(long employeeId) {
        this.employeeId = employeeId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Employee employee = (Employee) o;
        return employeeId == employee.employeeId && firstName.equals(employee.firstName) && lastName.equals(employee.lastName) && email.equals(employee.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(employeeId, firstName, lastName, email);
    }

    @Override
    public String toString() {
        return "Employee{" +
                "employeeId=" + employeeId +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
