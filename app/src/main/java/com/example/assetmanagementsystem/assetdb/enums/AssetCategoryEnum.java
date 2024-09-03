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

    public static AssetCategoryEnum fromString(String text) {
        for (AssetCategoryEnum category : AssetCategoryEnum.values()) {
            if (category.displayName.equalsIgnoreCase(text)) {
                return category;
            }
        }
        throw new IllegalArgumentException("No enum constant with text " + text + " found");
    }
}