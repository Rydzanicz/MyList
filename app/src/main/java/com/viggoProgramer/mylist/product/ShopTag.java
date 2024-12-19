package com.viggoProgramer.mylist.product;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "shop_tags")
public class ShopTag {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String tagName;

    public ShopTag(String tagName) {
        this.tagName = tagName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }
}
