package com.example.assetmanagementsystem.assetdb.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.assetmanagementsystem.assetdb.model.Employee;
import com.example.assetmanagementsystem.assetdb.model.Inventory;
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

    @Delete
    void deleteInventory(Inventory inventory);
    @Delete
    void deleteInventories(Inventory... inventories);
}
