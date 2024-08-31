package com.example.assetmanagementsystem.assetdb.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.example.assetmanagementsystem.util.Constants;

import java.io.Serializable;
import java.util.Objects;

@Entity(tableName = Constants.TABLE_NAME_INVENTORY,
        foreignKeys = {
                @ForeignKey(
                        entity = Employee.class,
                        parentColumns = "employee_id",
                        childColumns = "old_employee_id",
                        onDelete = ForeignKey.CASCADE
                ),
                @ForeignKey(
                        entity = Employee.class,
                        parentColumns = "employee_id",
                        childColumns = "new_employee_id",
                        onDelete = ForeignKey.CASCADE
                ),
                @ForeignKey(
                        entity = Location.class,
                        parentColumns = "location_id",
                        childColumns = "old_location_id",
                        onDelete = ForeignKey.CASCADE
                ),
                @ForeignKey(
                        entity = Location.class,
                        parentColumns = "location_id",
                        childColumns = "new_location_id",
                        onDelete = ForeignKey.CASCADE
                )
        })
public class Inventory implements Serializable {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "inventory_id")
    private int inventoryId;
    private long barcode;
    @ColumnInfo(name = "old_employee_id", index = true)
    private int oldEmployeeId;
    @ColumnInfo(name = "new_employee_id", index = true)
    private int newEmployeeId;
    @ColumnInfo(name = "old_location_id", index = true)
    private int oldLocationId;
    @ColumnInfo(name = "new_location_id", index = true)
    private int newLocationId;

    public Inventory(long barcode, int oldEmployeeId, int newEmployeeId, int oldLocationId, int newLocationId) {
        this.barcode = barcode;
        this.oldEmployeeId = oldEmployeeId;
        this.newEmployeeId = newEmployeeId;
        this.oldLocationId = oldLocationId;
        this.newLocationId = newLocationId;
    }

    @Ignore
    public Inventory() {
    }

    public int getInventoryId() {
        return inventoryId;
    }

    public void setInventoryId(int inventoryId) {
        this.inventoryId = inventoryId;
    }

    public long getBarcode() {
        return barcode;
    }

    public void setBarcode(long barcode) {
        this.barcode = barcode;
    }

    public int getOldEmployeeId() {
        return oldEmployeeId;
    }

    public void setOldEmployeeId(int oldEmployeeId) {
        this.oldEmployeeId = oldEmployeeId;
    }

    public int getNewEmployeeId() {
        return newEmployeeId;
    }

    public void setNewEmployeeId(int newEmployeeId) {
        this.newEmployeeId = newEmployeeId;
    }

    public int getOldLocationId() {
        return oldLocationId;
    }

    public void setOldLocationId(int oldLocationId) {
        this.oldLocationId = oldLocationId;
    }

    public int getNewLocationId() {
        return newLocationId;
    }

    public void setNewLocationId(int newLocationId) {
        this.newLocationId = newLocationId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Inventory inventory = (Inventory) o;
        return inventoryId == inventory.inventoryId && barcode == inventory.barcode && oldEmployeeId == inventory.oldEmployeeId && newEmployeeId == inventory.newEmployeeId && oldLocationId == inventory.oldLocationId && newLocationId == inventory.newLocationId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(inventoryId, barcode, oldEmployeeId, newEmployeeId, oldLocationId, newLocationId);
    }

    @Override
    public String toString() {
        return "Inventory{" +
                "inventoryId=" + inventoryId +
                ", barcode=" + barcode +
                ", oldEmployeeId=" + oldEmployeeId +
                ", newEmployeeId=" + newEmployeeId +
                ", oldLocationId=" + oldLocationId +
                ", newLocationId=" + newLocationId +
                '}';
    }
}
