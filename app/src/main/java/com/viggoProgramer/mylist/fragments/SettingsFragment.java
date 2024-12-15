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
import androidx.navigation.fragment.NavHostFragment;

import com.viggoProgramer.mylist.R;
import com.viggoProgramer.mylist.databinding.FragmentSettingsBinding;

public class SettingsFragment extends Fragment {

    private FragmentSettingsBinding binding;
    private final static String CODE = "VIGGO999";

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

        binding.buttonSaveCode.setOnClickListener(v -> {
            final String code = binding.editCode.getText()
                                                .toString()
                                                .trim();
            if (code.equals(CODE)) {
                saveCode(code);
                Toast.makeText(requireContext(), "Code saved!", Toast.LENGTH_SHORT)
                     .show();
            } else {
                Toast.makeText(requireContext(), "Enter the code again!", Toast.LENGTH_SHORT)
                     .show();
            }
        });
    }

    private void saveCode(final String code) {
        requireContext();
        requireContext().getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
                        .edit()
                        .putString("user_code", code)
                        .apply();
        NavHostFragment.findNavController(SettingsFragment.this)
                       .navigate(R.id.action_SettingsFragment_to_ListProductsFragment);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
