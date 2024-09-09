package com.example.assetmanagementsystem.assetdb.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.assetmanagementsystem.assetdb.model.Asset;
import com.example.assetmanagementsystem.assetdb.model.Employee;
import com.example.assetmanagementsystem.assetdb.model.Inventory;
import com.example.assetmanagementsystem.assetdb.model.Location;
import com.example.assetmanagementsystem.util.Constants;

import java.util.List;

@Dao
public interface InventoryDao {
    @Query("SELECT * FROM " + Constants.TABLE_NAME_INVENTORY)
    List<Inventory> getInventories();

    @Insert
    long insertInventory(Inventory inventory);

    @Update
    void updateInventory(Inventory inventory);

    @Query("DELETE FROM " + Constants.TABLE_NAME_INVENTORY + " WHERE inventory_id = :inventoryId")
    void deleteInventoryById(long inventoryId);
    @Delete
    void deleteInventories(Inventory... inventories);
    @Query("SELECT * FROM " + Constants.TABLE_NAME_EMPLOYEE + " WHERE employee_id = :employeeId")
    Employee getEmployeeById(long employeeId);
    @Query("SELECT * FROM " + Constants.TABLE_NAME_LOCATION + " WHERE location_id = :locationId")
    Location getLocationById(long locationId);

    @Query("SELECT * FROM " + Constants.TABLE_NAME_ASSET + " WHERE barcode = :barcode")
    Asset getAssetById(long barcode);

}
