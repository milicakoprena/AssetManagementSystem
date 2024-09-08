package com.example.assetmanagementsystem.assetdb.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.assetmanagementsystem.assetdb.model.Asset;
import com.example.assetmanagementsystem.util.Constants;

import java.util.List;

@Dao
public interface AssetDao {
    @Query("SELECT * FROM " + Constants.TABLE_NAME_ASSET)
    List<Asset> getAssets();

    @Insert
    long insertAsset(Asset asset);

    @Update
    void updateAsset(Asset asset);

    @Delete
    void deleteAsset(Asset asset);

    @Delete
    void deleteAssets(Asset... assets);

    @Query("SELECT * FROM " + Constants.TABLE_NAME_ASSET + " WHERE barcode = :barcode")
    Asset findById(long barcode);
}
