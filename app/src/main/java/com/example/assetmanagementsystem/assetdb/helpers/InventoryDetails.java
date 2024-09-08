package com.example.assetmanagementsystem.assetdb.helpers;

import com.example.assetmanagementsystem.assetdb.model.Asset;

public class InventoryDetails {
    private long inventoryId;
    private Asset asset;
    private String oldEmployee;
    private String newEmployee;
    private String oldLocation;
    private String newLocation;

    public InventoryDetails(long inventoryId, Asset asset, String oldEmployee, String newEmployee, String oldLocation, String newLocation) {
        this.inventoryId = inventoryId;
        this.asset = asset;
        this.oldEmployee = oldEmployee;
        this.newEmployee = newEmployee;
        this.oldLocation = oldLocation;
        this.newLocation = newLocation;
    }

    public long getInventoryId() {
        return inventoryId;
    }

    public void setInventoryId(long inventoryId) {
        this.inventoryId = inventoryId;
    }

    public Asset getAsset() {
        return asset;
    }

    public void setAsset(Asset asset) {
        this.asset = asset;
    }

    public String getOldEmployee() {
        return oldEmployee;
    }

    public void setOldEmployee(String oldEmployee) {
        this.oldEmployee = oldEmployee;
    }

    public String getNewEmployee() {
        return newEmployee;
    }

    public void setNewEmployee(String newEmployee) {
        this.newEmployee = newEmployee;
    }

    public String getOldLocation() {
        return oldLocation;
    }

    public void setOldLocation(String oldLocation) {
        this.oldLocation = oldLocation;
    }

    public String getNewLocation() {
        return newLocation;
    }

    public void setNewLocation(String newLocation) {
        this.newLocation = newLocation;
    }
}
