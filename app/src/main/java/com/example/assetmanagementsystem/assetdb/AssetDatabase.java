package com.example.assetmanagementsystem.assetdb;

import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.room.Database;

import com.example.assetmanagementsystem.assetdb.dao.AssetDao;
import com.example.assetmanagementsystem.assetdb.dao.EmployeeDao;
import com.example.assetmanagementsystem.assetdb.dao.InventoryDao;
import com.example.assetmanagementsystem.assetdb.dao.LocationDao;
import com.example.assetmanagementsystem.assetdb.model.Asset;
import com.example.assetmanagementsystem.assetdb.model.Employee;
import com.example.assetmanagementsystem.assetdb.model.Inventory;
import com.example.assetmanagementsystem.assetdb.model.Location;
import com.example.assetmanagementsystem.util.Constants;
import com.example.assetmanagementsystem.util.DateRoomConverter;

import android.content.Context;

@Database(entities = { Asset.class, Employee.class, Location.class, Inventory.class }, version = 5, exportSchema = false)
@TypeConverters({DateRoomConverter.class})
public abstract class AssetDatabase extends RoomDatabase {

    public abstract AssetDao getAssetDao();
    public abstract EmployeeDao getEmployeeDao();
    public abstract LocationDao getLocationDao();
    public abstract InventoryDao getInventoryDao();
    private static AssetDatabase assetDb;

    // synchronized is use to avoid concurrent access in multithred environment
    public static /*synchronized*/ AssetDatabase getInstance(Context context) {
        if (null == assetDb) {
            assetDb = buildDatabaseInstance(context);
        }
        return assetDb;
    }

    private static AssetDatabase buildDatabaseInstance(Context context) {
        return Room.databaseBuilder(context,
                AssetDatabase.class,
                Constants.DB_NAME).allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build();
    }

    public  void cleanUp(){
        assetDb = null;
    }
}