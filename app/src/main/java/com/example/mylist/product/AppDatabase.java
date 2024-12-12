package com.example.mylist.product;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import androidx.room.Room;
import android.content.Context;

@Database(entities = {Product.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase instance;

    public abstract ProductDao productDao();

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "product_database")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}
