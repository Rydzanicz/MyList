package com.example.mylist.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.room.Room;

import com.example.mylist.product.AppDatabase;
import com.example.mylist.product.Product;
import com.example.mylist.product.ProductAdapter;
import com.example.mylist.R;
import com.example.mylist.databinding.FragmentListProductsBinding;

import java.util.List;

public class ListProductsFragment extends Fragment {

    private FragmentListProductsBinding binding;
    private AppDatabase database;
    private ProductAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentListProductsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        database = Room.databaseBuilder(requireContext(), AppDatabase.class, "product_database")
                .allowMainThreadQueries()
                .build();

        final List<Product> productList = database.productDao().getAllProducts();

        adapter = new ProductAdapter(productList);
        binding.recyclerViewProducts.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        binding.recyclerViewProducts.setAdapter(adapter);

        binding.fabAddProduct.setOnClickListener(v ->
                NavHostFragment.findNavController(ListProductsFragment.this)
                        .navigate(R.id.action_ListProductsFragment_to_AddProductFragment)
        );
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
