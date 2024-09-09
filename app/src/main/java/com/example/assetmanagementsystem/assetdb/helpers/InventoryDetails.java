package com.example.assetmanagementsystem.assetdb.helpers;
import com.example.assetmanagementsystem.assetdb.model.Asset;
import com.example.assetmanagementsystem.assetdb.model.Inventory;

public class InventoryDetails {
    private Inventory inventory;
    private Asset asset;
    private String oldEmployee;
    private String newEmployee;
    private String oldLocation;
    private String newLocation;

    public InventoryDetails(Inventory inventory, Asset asset, String oldEmployee, String newEmployee, String oldLocation, String newLocation) {
        this.inventory = inventory;
        this.asset = asset;
        this.oldEmployee = oldEmployee;
        this.newEmployee = newEmployee;
        this.oldLocation = oldLocation;
        this.newLocation = newLocation;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
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
