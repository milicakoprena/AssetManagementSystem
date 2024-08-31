package com.example.assetmanagementsystem.assetdb.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.assetmanagementsystem.assetdb.model.Location;
import com.example.assetmanagementsystem.util.Constants;

import java.util.List;

@Dao
public interface LocationDao {
    @Query("SELECT * FROM " + Constants.TABLE_NAME_LOCATION)
    List<Location> getLocations();

    @Insert
    long insertLocation(Location location);

    @Update
    void updateLocation(Location location);

    @Delete
    void deleteLocation(Location location);
    @Delete
    void deleteLocations(Location... locations);
}
