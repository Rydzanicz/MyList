package com.example.mylist.product;

import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mylist.R;

public class ProductViewHolder extends RecyclerView.ViewHolder {
    final ImageView imageProduct;
    final TextView textName, textCompany, textPrice, textShop, textCategory, textNotes;
    final RatingBar ratingBar;

    public ProductViewHolder(@NonNull View itemView) {
        super(itemView);
        imageProduct = itemView.findViewById(R.id.imageProduct);
        textName     = itemView.findViewById(R.id.textName);
        textCompany  = itemView.findViewById(R.id.textCompany);
        textPrice    = itemView.findViewById(R.id.textPrice);
        textShop     = itemView.findViewById(R.id.textShop);
        textCategory = itemView.findViewById(R.id.textCategory);
        textNotes    = itemView.findViewById(R.id.textNotes);
        ratingBar    = itemView.findViewById(R.id.ratingBar);
    }
}
