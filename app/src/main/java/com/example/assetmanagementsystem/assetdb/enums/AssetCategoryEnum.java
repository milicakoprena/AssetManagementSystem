package com.example.assetmanagementsystem.assetdb.enums;

public enum AssetCategoryEnum {
    FURNITURE("Furniture"),
    COMPUTERS_AND_TECH("Technical Equipment"),
    ELECTRICAL_DEVICES("Electrical Devices"),
    OFFICE_SUPPLIES("Office Supplies"),
    OTHER("Other");

    private final String displayName;

    AssetCategoryEnum(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}