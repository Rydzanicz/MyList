package com.example.mylist.product;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ProductDao {
    @Insert
    void insertProduct(Product product);

    @Query("SELECT * FROM products")
    List<Product> getAllProducts();

    @Query("DELETE FROM products WHERE id = :productId")
    void deleteProductById(int productId);
}
