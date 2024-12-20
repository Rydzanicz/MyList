package com.viggoProgramer.mylist.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.viggoProgramer.mylist.R;
import com.viggoProgramer.mylist.databinding.FragmentSettingsBinding;
import com.viggoProgramer.mylist.product.AppDatabase;
import com.viggoProgramer.mylist.product.ShopTag;

import java.util.ArrayList;
import java.util.List;

public class SettingsFragment extends Fragment {

    private FragmentSettingsBinding binding;
    private AppDatabase database;
    private List<ShopTag> shopTags;

    @Nullable
    @Override
    public View onCreateView(final @NonNull LayoutInflater inflater,
                             final @Nullable ViewGroup container,
                             final @Nullable Bundle savedInstanceState) {
        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(final @NonNull View view,
                              final @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        database = AppDatabase.getInstance(requireContext());
        shopTags = new ArrayList<>();

        loadTags();

        binding.buttonDeleteSelectedTags.setOnClickListener(v -> {
            deleteSelectedTags();
            loadTags();
        });
    }

    private void loadTags() {
        new Thread(() -> {
            shopTags = database.shopTagDao().getAllShopTags();
            requireActivity().runOnUiThread(this::populateTagChips);
        }).start();
    }

    private void populateTagChips() {
        binding.chipGroupTags.removeAllViews();
        for (ShopTag tag : shopTags) {
            addChipToGroup(tag);
        }
    }

    private void addChipToGroup(ShopTag tag) {
        Chip chip = new Chip(requireContext());
        chip.setText(tag.getTagName());
        chip.setCheckable(true);
        chip.setTag(tag);
        binding.chipGroupTags.addView(chip);
    }

    private void deleteSelectedTags() {
        List<ShopTag> tagsToDelete = new ArrayList<>();

        for (int i = 0; i < binding.chipGroupTags.getChildCount(); i++) {
            View chipView = binding.chipGroupTags.getChildAt(i);
            if (chipView instanceof Chip) {
                Chip chip = (Chip) chipView;
                if (chip.isChecked()) {
                    tagsToDelete.add((ShopTag) chip.getTag());
                }
            }
        }

        if (tagsToDelete.isEmpty()) {
            Toast.makeText(requireContext(), "No tags selected for deletion.", Toast.LENGTH_SHORT).show();
            return;
        }

        new Thread(() -> {
            for (ShopTag tag : tagsToDelete) {
                database.shopTagDao().deleteShopTag(tag);
            }
            requireActivity().runOnUiThread(() -> {
                Toast.makeText(requireContext(), "Selected tags deleted successfully.", Toast.LENGTH_SHORT).show();
                loadTags();
            });
        }).start();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
