package com.viggoProgramer.mylist.product;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.List;

@Entity(tableName = "products")
public class Product {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private String company;
    private String name;
    private List<String> shop;
    private double price;
    private float rating;
    private String notes;
    private String photoPath;
    private String category;

    public Product(final String company,
                   final String name,
                   final List<String> shop,
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

    public void setId(final int id) {
        this.id = id;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(final String company) {
        this.company = company;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public List<String> getShop() {
        return shop;
    }

    public void setShop(final List<String> shop) {
        this.shop = shop;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(final double price) {
        this.price = price;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(final float rating) {
        this.rating = rating;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(final String notes) {
        this.notes = notes;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(final String photoPath) {
        this.photoPath = photoPath;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(final String category) {
        this.category = category;
    }
}
