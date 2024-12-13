package com.example.mylist.product;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ProductDao {
    @Insert
    void insertProduct(Product product);

    @Query("SELECT * FROM products")
    List<Product> getAllProducts();

    @Query("DELETE FROM products WHERE id = :productId")
    void deleteProductById(int productId);

    @Query("SELECT * FROM products WHERE id = :productId LIMIT 1")
    Product getProductById(int productId);

    @Update
    void updateProduct(Product product);

    @Delete
    void deleteProduct(Product product);
}