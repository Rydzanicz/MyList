package com.example.mylist.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.room.Room;

import com.example.mylist.R;
import com.example.mylist.databinding.FragmentListProductsBinding;
import com.example.mylist.product.AppDatabase;
import com.example.mylist.product.Product;
import com.example.mylist.product.ProductAdapter;

import java.util.ArrayList;
import java.util.List;

public class ListProductsFragment extends Fragment {

    private FragmentListProductsBinding binding;
    private AppDatabase database;
    private ProductAdapter adapter;
    private List<Product> productList;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentListProductsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializeDatabase();
        setupRecyclerView();
        setupFabButton();
        setupSortButton();
        setupSearchFunctionality();
    }

    private void initializeDatabase() {
        database    = Room.databaseBuilder(requireContext(), AppDatabase.class, "product_database")
                          .allowMainThreadQueries()
                          .build();
        productList = database.productDao()
                              .getAllProducts();
    }

    private void setupRecyclerView() {
        adapter = new ProductAdapter(productList, requireContext());
        binding.recyclerViewProducts.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        binding.recyclerViewProducts.setAdapter(adapter);
    }

    private void setupFabButton() {
        binding.fabAddProduct.setOnClickListener(v -> NavHostFragment.findNavController(this)
                                                                     .navigate(R.id.action_ListProductsFragment_to_AddProductFragment));
    }

    private void setupSortButton() {
        binding.buttonSort.setOnClickListener(v -> showSortOptions());
    }

    private void setupSearchFunctionality() {
        binding.editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(final CharSequence s,
                                          final int start,
                                          final int count,
                                          final int after) {
            }

            @Override
            public void onTextChanged(final CharSequence s,
                                      final int start,
                                      final int before,
                                      final int count) {
                filterProducts(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void filterProducts(final String query) {
        List<Product> filteredList = new ArrayList<>();
        for (Product product : productList) {
            if (product.getName()
                       .toLowerCase()
                       .startsWith(query.toLowerCase())) {
                filteredList.add(product);
            }
        }
        adapter.updateData(filteredList);
    }

    private void showSortOptions() {
        String[] options = {getString(R.string.sort_company),
                            getString(R.string.sort_name),
                            getString(R.string.sort_shop)};

        new com.google.android.material.dialog.MaterialAlertDialogBuilder(requireContext()).setTitle(R.string.sort_products)
                                                                                           .setItems(options,
                                                                                                     (dialog, which) -> handleSortSelection(which))
                                                                                           .show();
    }

    private void handleSortSelection(int which) {
        switch (which) {
            case 0:
                sortProducts("company");
                break;
            case 1:
                sortProducts("name");
                break;
            case 2:
                sortProducts("shop");
                break;
            default:
                Toast.makeText(requireContext(), R.string.unknown_sorting_criterion, Toast.LENGTH_SHORT)
                     .show();
        }
    }

    private void sortProducts(String criterion) {
        if (productList == null || productList.isEmpty()) {
            Toast.makeText(requireContext(), R.string.no_products_to_sort, Toast.LENGTH_SHORT)
                 .show();
            return;
        }

        switch (criterion) {
            case "company":
                productList.sort((p1, p2) -> p1.getCompany()
                                               .compareToIgnoreCase(p2.getCompany()));
                break;
            case "name":
                productList.sort((p1, p2) -> p1.getName()
                                               .compareToIgnoreCase(p2.getName()));
                break;
            case "shop":
                productList.sort((p1, p2) -> p1.getShop()
                                               .compareToIgnoreCase(p2.getShop()));
                break;
        }

        adapter.updateData(productList);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
