package com.viggoProgramer.mylist.product;

import androidx.room.TypeConverter;

import java.util.Arrays;
import java.util.List;

public class Converters {

    @TypeConverter
    public String fromList(List<String> list) {
        return list != null ? String.join(",", list) : null;
    }

    @TypeConverter
    public List<String> toList(String concatenated) {
        return concatenated != null ? Arrays.asList(concatenated.split(",")) : null;
    }
}
