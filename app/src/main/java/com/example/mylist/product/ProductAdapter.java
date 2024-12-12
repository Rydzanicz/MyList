package com.example.mylist.product;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mylist.R;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductViewHolder> {

    private final List<Product> productList;

    public ProductAdapter(List<Product> productList) {
        this.productList = productList;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);

        holder.textName.setText(product.getName());
        holder.textCompany.setText("Company: " + product.getCompany());
        holder.textPrice.setText(String.format("Price: $%.2f", product.getPrice()));
        holder.textShop.setText("Shop: " + product.getShop());
        holder.textCategory.setText("Category: " + product.getCategory());
        holder.textNotes.setText("Notes: " + product.getNotes());
        holder.ratingBar.setRating(product.getRating());

        if (product.getPhotoPath() != null) {
            Glide.with(holder.itemView.getContext())
                    .load(product.getPhotoPath())
                    .placeholder(R.drawable.ic_placeholder)
                    .into(holder.imageProduct);
        } else {
            holder.imageProduct.setImageResource(R.drawable.ic_placeholder);
        }
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

}
