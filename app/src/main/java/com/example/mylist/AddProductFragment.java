package com.example.mylist;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.mylist.databinding.FragmentAddProductBinding;

import java.util.ArrayList;
import java.util.List;

public class AddProductFragment extends Fragment {

    private FragmentAddProductBinding binding;

    private static final List<Product> productList = new ArrayList<>();

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentAddProductBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.buttonCancel.setOnClickListener(v ->
                NavHostFragment.findNavController(AddProductFragment.this)
                        .navigate(R.id.action_AddProductFragment_to_ListProductsFragment)
        );

        binding.buttonSave.setOnClickListener(v -> {
            if (validateInputs()) {
                saveProduct();
                NavHostFragment.findNavController(AddProductFragment.this)
                        .navigate(R.id.action_AddProductFragment_to_ListProductsFragment);
            }
        });

        binding.buttonAddPhoto.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Dodawanie zdjęcia - funkcja do implementacji", Toast.LENGTH_SHORT).show();
        });
    }

    private boolean validateInputs() {
        if (TextUtils.isEmpty(binding.editCompany.getText())) {
            Toast.makeText(getContext(), "Company cannot be null or empty", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(binding.editName.getText())) {
            Toast.makeText(getContext(), "Name cannot be null or empty", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(binding.editPrice.getText())) {
            Toast.makeText(getContext(), "Price cannot be null or empty", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(binding.editShop.getText())) {
            Toast.makeText(getContext(), "Shop cannot be null or empty", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void saveProduct() {
        final String company = binding.editCompany.getText().toString();
        final String name = binding.editName.getText().toString();
        final String shop = binding.editShop.getText().toString();
        final double price = Double.parseDouble(binding.editPrice.getText().toString());
        final float rating = binding.ratingBar.getRating();
        final String notes = binding.editNotes.getText().toString();

        final String photo = "placeholder";

        final Product product = new Product(company, name, shop, price, rating, notes, photo);
        productList.add(product);

        Toast.makeText(getContext(), "Produkt został zapisany!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
