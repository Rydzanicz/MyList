package com.viggoProgramer.mylist.product;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.viggoProgramer.mylist.R;

import java.util.ArrayList;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductViewHolder> {

    private final List<Product> productList;
    private final Context context;

    public ProductAdapter(final List<Product> productList,
                          final Context context) {
        this.productList = new ArrayList<>(productList);
        this.context     = context;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(final @NonNull ViewGroup parent,
                                                final int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                                  .inflate(R.layout.item_product, parent, false);

        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final @NonNull ProductViewHolder holder,
                                 final int position) {
        Product product = productList.get(position);

        holder.textName.setText(product.getName());
        holder.textCompany.setText(context.getString(R.string.company) + product.getCompany());
        holder.textPrice.setText(context.getString(R.string.price) + String.format("%.2f", product.getPrice()));
        holder.textShop.setText(context.getString(R.string.shop) + product.getShop());
        holder.textCategory.setText(context.getString(R.string.category) + product.getCategory());
        holder.textNotes.setText(context.getString(R.string.notes) + product.getNotes());
        holder.ratingBar.setRating(product.getRating());

        if (product.getPhotoPath() != null) {
            Glide.with(holder.itemView.getContext())
                 .load(product.getPhotoPath())
                 .placeholder(R.drawable.ic_placeholder)
                 .into(holder.imageProduct);
        } else {
            holder.imageProduct.setImageResource(R.drawable.ic_placeholder);
        }

        holder.itemView.setOnClickListener(v -> {
            final Bundle args = new Bundle();
            args.putInt("productId", product.getId());
            Navigation.findNavController(v)
                      .navigate(R.id.action_ListProductsFragment_to_EditProductFragment, args);
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public void updateData(final List<Product> newProducts) {
        this.productList.clear();
        this.productList.addAll(newProducts);
        notifyDataSetChanged();
    }
}
