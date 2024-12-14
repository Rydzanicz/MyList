package com.viggoProgramer.mylist.product;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "products")
public class Product {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private String company;
    private String name;
    private String shop;
    private double price;
    private float rating;
    private String notes;
    private String photoPath;
    private String category;

    public Product(final String company,
                   final String name,
                   final String shop,
                   final double price,
                   final float rating,
                   final String notes,
                   final String photoPath,
                   final String category) {
        this.company   = company;
        this.name      = name;
        this.shop      = shop;
        this.price     = price;
        this.rating    = rating;
        this.notes     = notes;
        this.photoPath = photoPath;
        this.category  = category;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShop() {
        return shop;
    }

    public void setShop(String shop) {
        this.shop = shop;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
