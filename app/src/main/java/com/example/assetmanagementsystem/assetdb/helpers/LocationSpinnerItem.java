package com.example.assetmanagementsystem.assetdb.helpers;

public class LocationSpinnerItem {
    private long locationId;
    private String name;

    public LocationSpinnerItem(long locationId, String name) {
        this.locationId = locationId;
        this.name = name;
    }

    public long getLocationId() {
        return locationId;
    }

    public void setLocationId(long locationId) {
        this.locationId = locationId;
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
