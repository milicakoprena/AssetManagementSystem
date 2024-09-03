package com.example.assetmanagementsystem.assetdb.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.example.assetmanagementsystem.assetdb.enums.AssetCategoryEnum;
import com.example.assetmanagementsystem.util.Constants;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@Entity(tableName = Constants.TABLE_NAME_ASSET,
        foreignKeys = {
                @ForeignKey(entity = Employee.class,
                        parentColumns = "employee_id",
                        childColumns = "employee_id",
                        onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Location.class,
                        parentColumns = "location_id",
                        childColumns = "location_id",
                        onDelete = ForeignKey.CASCADE)
        })
public class Asset implements Serializable {
    @PrimaryKey
    private long barcode;
    private String name;
    private String description;
    private double price;
    private AssetCategoryEnum category;
    @ColumnInfo(name = "date_created")
    private Date dateCreated;
    @ColumnInfo(name = "employee_id", index = true)
    private long employeeId;
    @ColumnInfo(name = "location_id", index = true)
    private long locationId;
    @ColumnInfo(name = "image_url")
    private String imageUrl;

    public Asset(long barcode, String name, String description, double price, AssetCategoryEnum category, long employeeId, long locationId, String imageUrl) {
        this.barcode = barcode;
        this.name = name;
        this.description = description;
        this.price = price;
        this.category = category;
        this.employeeId = employeeId;
        this.locationId = locationId;
        this.imageUrl = imageUrl;
        this.dateCreated = new Date(System.currentTimeMillis());
    }

    @Ignore
    public Asset() {
    }

    public long getBarcode() {
        return barcode;
    }

    public void setBarcode(long barcode) {
        this.barcode = barcode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public AssetCategoryEnum getCategory() {
        return category;
    }

    public void setCategory(AssetCategoryEnum category) {
        this.category = category;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(long employeeId) {
        this.employeeId = employeeId;
    }

    public long getLocationId() {
        return locationId;
    }

    public void setLocationId(long locationId) {
        this.locationId = locationId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Asset asset = (Asset) o;
        return barcode == asset.barcode && Double.compare(asset.price, price) == 0 && employeeId == asset.employeeId && locationId == asset.locationId && name.equals(asset.name) && description.equals(asset.description) && dateCreated.equals(asset.dateCreated) && imageUrl.equals(asset.imageUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(barcode, name, description, price, dateCreated, employeeId, locationId, imageUrl);
    }

    @Override
    public String toString() {
        return "Asset{" +
                "barcode=" + barcode +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", dateCreated=" + dateCreated +
                ", employeeId=" + employeeId +
                ", locationId=" + locationId +
                ", imageUrl='" + imageUrl + '\'' +
                '}';
    }
}
