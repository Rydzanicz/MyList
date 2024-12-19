package com.viggoProgramer.mylist.product;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Delete;

import java.util.List;

@Dao
public interface ShopTagDao {
    @Insert
    void insertShopTag(ShopTag shopTag);

    @Query("SELECT * FROM shop_tags")
    List<ShopTag> getAllShopTags();

    @Delete
    void deleteShopTag(ShopTag shopTag);
}
