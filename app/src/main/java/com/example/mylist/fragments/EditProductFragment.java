package com.example.mylist.fragments;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.mylist.R;
import com.example.mylist.databinding.FragmentEditProductBinding;
import com.example.mylist.product.AppDatabase;
import com.example.mylist.product.Product;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EditProductFragment extends Fragment {

    private FragmentEditProductBinding binding;
    private AppDatabase database;
    private Product product;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private Uri photoUri;

    @Override
    public View onCreateView(final @NonNull LayoutInflater inflater,
                             final ViewGroup container,
                             final Bundle savedInstanceState) {
        binding = FragmentEditProductBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(final @NonNull View view,
                              final @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        database = AppDatabase.getInstance(requireContext());

        if (getArguments() != null) {
            final int productId = getArguments().getInt("productId");
            loadProduct(productId);
        }

        binding.buttonSaveChanges.setOnClickListener(v -> {
            if (validateInputs()) {
                updateProduct();
            }
        });

        binding.buttonChangePhoto.setOnClickListener(v -> openGallery());
        binding.buttonTakePhoto.setOnClickListener(v -> openCamera());
    }

    private void loadProduct(final int productId) {
        executor.execute(() -> {
            product = database.productDao()
                              .getProductById(productId);
            if (product != null) {
                requireActivity().runOnUiThread(this::populateFields);
            }
        });
    }

    private void populateFields() {
        binding.editTextName.setText(product.getName());
        binding.editTextCompany.setText(product.getCompany());
        binding.editTextPrice.setText(String.valueOf(product.getPrice()));
        binding.editTextShop.setText(product.getShop());
        binding.editTextCategory.setText(product.getCategory());
        binding.editTextNotes.setText(product.getNotes());
        binding.ratingBar.setRating(product.getRating());

        if (product.getPhotoPath() != null) {
            binding.imageProduct.setImageURI(Uri.parse(product.getPhotoPath()));
        } else {
            binding.imageProduct.setImageResource(R.drawable.ic_placeholder);
        }
    }

    private boolean validateInputs() {
        if (TextUtils.isEmpty(binding.editTextName.getText())) {
            Toast.makeText(getContext(), "Name cannot be empty", Toast.LENGTH_SHORT)
                 .show();
            return false;
        }
        if (TextUtils.isEmpty(binding.editTextCompany.getText())) {
            Toast.makeText(getContext(), "Company cannot be empty", Toast.LENGTH_SHORT)
                 .show();
            return false;
        }
        if (TextUtils.isEmpty(binding.editTextPrice.getText())) {
            Toast.makeText(getContext(), "Price cannot be empty", Toast.LENGTH_SHORT)
                 .show();
            return false;
        }
        if (TextUtils.isEmpty(binding.editTextShop.getText())) {
            Toast.makeText(getContext(), "Shop cannot be empty", Toast.LENGTH_SHORT)
                 .show();
            return false;
        }
        if (TextUtils.isEmpty(binding.editTextCategory.getText())) {
            Toast.makeText(getContext(), "Category cannot be empty", Toast.LENGTH_SHORT)
                 .show();
            return false;
        }
        return true;
    }

    private void updateProduct() {
        product.setName(binding.editTextName.getText()
                                            .toString());
        product.setCompany(binding.editTextCompany.getText()
                                                  .toString());
        product.setPrice(Double.parseDouble(binding.editTextPrice.getText()
                                                                 .toString()));
        product.setShop(binding.editTextShop.getText()
                                            .toString());
        product.setCategory(binding.editTextCategory.getText()
                                                    .toString());
        product.setNotes(binding.editTextNotes.getText()
                                              .toString());
        product.setRating(binding.ratingBar.getRating());

        if (photoUri != null) {
            try {
                product.setPhotoPath(saveImageToAppFolder(photoUri));
            } catch (IOException e) {
                Toast.makeText(getContext(), "Failed to save photo", Toast.LENGTH_SHORT)
                     .show();
            }
        }

        executor.execute(() -> {
            database.productDao()
                    .updateProduct(product);
            requireActivity().runOnUiThread(() -> {
                Toast.makeText(getContext(), "Product updated!", Toast.LENGTH_SHORT)
                     .show();
                NavHostFragment.findNavController(EditProductFragment.this)
                               .navigate(R.id.action_EditProductFragment_to_ListProductsFragment);
            });
        });
    }

    private void openGallery() {
        Intent pickPhotoIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhotoIntent, 1001);
    }

    private void openCamera() {
        final Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
            final File photoFile = createImageFile();
            if (photoFile != null) {
                try {
                    photoUri = FileProvider.getUriForFile(requireContext(), requireActivity().getPackageName() + ".fileprovider", photoFile);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                    startActivityForResult(takePictureIntent, 1002);
                } catch (IllegalArgumentException e) {
                    Log.e("EditProductFragment", "FileProvider configuration error: " + e.getMessage(), e);
                    Toast.makeText(requireContext(), "Error accessing file. Check configuration.", Toast.LENGTH_SHORT)
                         .show();
                }
            }
        } else {
            Toast.makeText(requireContext(), "No camera application found", Toast.LENGTH_SHORT)
                 .show();
        }
    }


    private File createImageFile() {
        final File storageDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (storageDir != null && !storageDir.exists()) {
            if (!storageDir.mkdirs()) {
                Toast.makeText(requireContext(), "Failed to create directory", Toast.LENGTH_SHORT)
                     .show();
                return null;
            }
        }
        final String fileName = "IMG_" + System.currentTimeMillis() + ".jpg";
        return new File(storageDir, fileName);
    }


    private String saveImageToAppFolder(final Uri imageUri) throws IOException {
        final InputStream inputStream = requireContext().getContentResolver()
                                                        .openInputStream(imageUri);
        final File appFolder = new File(requireContext().getFilesDir(), "images");
        if (!appFolder.exists()) {
            appFolder.mkdirs();
        }
        final File photoFile = new File(appFolder, "IMG_" + System.currentTimeMillis() + ".jpg");
        final OutputStream outputStream = new FileOutputStream(photoFile);
        final byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) > 0) {
            outputStream.write(buffer, 0, length);
        }
        inputStream.close();
        outputStream.close();
        return photoFile.getAbsolutePath();
    }

    @Override
    public void onActivityResult(final int requestCode,
                                 final int resultCode,
                                 final @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        getActivity();
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 1001 && data != null && data.getData() != null) {
                photoUri = data.getData();
                binding.imageProduct.setImageURI(photoUri);
            } else {
                if (requestCode == 1002) {
                    binding.imageProduct.setImageURI(photoUri);
                }
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        executor.shutdown();
    }
}
