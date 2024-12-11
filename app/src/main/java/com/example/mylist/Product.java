package com.example.mylist;

public class Product {
    private final String company;
    private final String name;
    private final String shop;
    private final double price;
    private final float rating;
    private final String notes;
    private final String photo;

    public Product(String company, String name, String shop, double price, float rating, String notes, String photo) {
        this.company = company;
        this.name = name;
        this.shop = shop;
        this.price = price;
        this.rating = rating;
        this.notes = notes;
        this.photo = photo;
    }

    public String getCompany() {
        return company;
    }

    public String getName() {
        return name;
    }

    public String getShop() {
        return shop;
    }

    public double getPrice() {
        return price;
    }

    public float getRating() {
        return rating;
    }

    public String getNotes() {
        return notes;
    }

    public String getPhoto() {
        return photo;
    }
}
