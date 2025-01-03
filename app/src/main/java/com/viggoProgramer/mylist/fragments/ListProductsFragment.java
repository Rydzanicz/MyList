package com.viggoProgramer.mylist.fragments;

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

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.viggoProgramer.mylist.R;
import com.viggoProgramer.mylist.databinding.FragmentListProductsBinding;
import com.viggoProgramer.mylist.product.AppDatabase;
import com.viggoProgramer.mylist.product.Product;
import com.viggoProgramer.mylist.product.ProductAdapter;
import com.viggoProgramer.mylist.product.ShopTag;

import java.util.ArrayList;
import java.util.List;

public class ListProductsFragment extends Fragment {

    private FragmentListProductsBinding binding;
    private AppDatabase database;
    private ProductAdapter adapter;
    private List<Product> productList;
    private String currentSortCriterion = "Sort";
    private ChipGroup chipGroupShopTags;
    private List<String> selectedTags;

    @Override
    public View onCreateView(final @NonNull LayoutInflater inflater,
                             final ViewGroup container,
                             final Bundle savedInstanceState) {
        binding = FragmentListProductsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(final @NonNull View view,
                              final @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.buttonSort.setText(currentSortCriterion);

        initializeDatabase();
        setupRecyclerView();
        setupFabButton();
        setupSortButton();
        setupSearchFunctionality();
        selectedTags = new ArrayList<>();
        chipGroupShopTags = view.findViewById(R.id.chipGroupShopTags);

        loadTags();
    }

    private void initializeDatabase() {
        database    = Room.databaseBuilder(requireContext(), AppDatabase.class, "product_database")
                          .allowMainThreadQueries()
                          .build();
        productList = database.productDao()
                              .getAllProducts();
    }
    private void loadTags() {
        new Thread(() -> {
            List<ShopTag> tags = database.shopTagDao().getAllShopTags();
            requireActivity().runOnUiThread(() -> {
                if (chipGroupShopTags == null) {
                    return;
                }
                chipGroupShopTags.removeAllViews();
                for (ShopTag tag : tags) {
                    addChipToChipGroup(tag.getTagName());
                }
            });
        }).start();
    }

    private void addChipToChipGroup(String tagName) {
        Chip chip = new Chip(requireContext());
        chip.setText(tagName);
        chip.setCheckable(true);
        chip.setChipStrokeWidth(1f);
        chip.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                selectedTags.add(tagName);
            } else {
                selectedTags.remove(tagName);
            }
        });
        chipGroupShopTags.addView(chip);
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
            public void afterTextChanged(final Editable s) {
            }
        });
    }

    private void filterProducts(final String query) {
        final List<Product> filteredList = new ArrayList<>();
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
        final String[] options = {getString(R.string.sort_company),
                                  getString(R.string.sort_name),
                                  getString(R.string.sort_category)};

        new com.google.android.material.dialog.MaterialAlertDialogBuilder(requireContext()).setTitle(R.string.sort_products)
                                                                                           .setItems(options,
                                                                                                     (dialog, which) -> handleSortSelection(which))
                                                                                           .show();
    }

    private void handleSortSelection(final int which) {
        switch (which) {
            case 0:
                currentSortCriterion = "Company";
                sortProducts("company");
                break;
            case 1:
                currentSortCriterion = "Name";
                sortProducts("name");
                break;
            case 2:
                currentSortCriterion = "Category";
                sortProducts("category");
                break;
            default:
                Toast.makeText(requireContext(), R.string.unknown_sorting_criterion, Toast.LENGTH_SHORT)
                     .show();
        }
        updateSortButtonText();
    }

    private void updateSortButtonText() {
        binding.buttonSort.setText(currentSortCriterion);
    }


    private void sortProducts(final String criterion) {
        if (productList == null || productList.isEmpty()) {
            Toast.makeText(requireContext(), R.string.no_products_to_sort, Toast.LENGTH_SHORT)
                 .show();
            return;
        }

        switch (criterion) {
            case "name":
                productList.sort((p1, p2) -> p1.getName()
                                               .compareToIgnoreCase(p2.getName()));
                break;
            case "company":
                productList.sort((p1, p2) -> p1.getCompany()
                                               .compareToIgnoreCase(p2.getCompany()));
                break;
            case "category":
                productList.sort((p1, p2) -> p1.getCategory()
                                               .compareToIgnoreCase(p2.getCategory()));
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
