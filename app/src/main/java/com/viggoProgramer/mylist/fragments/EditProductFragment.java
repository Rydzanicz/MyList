package com.viggoProgramer.mylist.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.viggoProgramer.mylist.R;
import com.viggoProgramer.mylist.ads.AdManager;
import com.viggoProgramer.mylist.databinding.FragmentEditProductBinding;
import com.viggoProgramer.mylist.product.AppDatabase;
import com.viggoProgramer.mylist.product.Product;

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
    private ActivityResultLauncher<String> requestPermissionLauncher;
    private ActivityResultLauncher<Intent> takePictureLauncher;
    private ActivityResultLauncher<Intent> pickFromGalleryLauncher;

    @Override
    public View onCreateView(final @NonNull LayoutInflater inflater,
                             final ViewGroup container,
                             final Bundle savedInstanceState) {
        binding                   = FragmentEditProductBinding.inflate(inflater, container, false);
        requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
            if (isGranted) {
                Toast.makeText(getContext(), "Permission granted!", Toast.LENGTH_SHORT)
                     .show();
            } else {
                Toast.makeText(getContext(), "Permission denied. Cannot proceed.", Toast.LENGTH_SHORT)
                     .show();
            }
        });

        takePictureLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            requireActivity();
            if (result.getResultCode() == Activity.RESULT_OK) {
                binding.imageProduct.setImageURI(photoUri);
                Toast.makeText(getContext(), "Photo saved: " + photoUri.getPath(), Toast.LENGTH_SHORT)
                     .show();
            } else {
                Toast.makeText(getContext(), "No photo taken", Toast.LENGTH_SHORT)
                     .show();
            }
        });

        pickFromGalleryLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            requireActivity();
            if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                final Uri selectedImageUri = result.getData()
                                                   .getData();
                binding.imageProduct.setImageURI(selectedImageUri);
                photoUri = selectedImageUri;
                Toast.makeText(getContext(), "Photo selected from gallery", Toast.LENGTH_SHORT)
                     .show();
            }
        });
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
        AdManager.loadInterstitialAd(requireContext());

        binding.buttonSaveChanges.setOnClickListener(v -> {
            if (validateInputs()) {
                saveChanges();
                AdManager.showInterstitialAd(requireContext(), () -> {
                    NavHostFragment.findNavController(this)
                                   .navigate(R.id.action_EditProductFragment_to_ListProductsFragment);
                });
            }
        });

        binding.buttonChangePhoto.setOnClickListener(v -> openGallery());
        binding.buttonTakePhoto.setOnClickListener(v -> openCamera());
        binding.buttonDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(requireContext()).setTitle("Confirm Delete")
                                                     .setMessage("Are you sure you want to delete this product?")
                                                     .setPositiveButton("Yes", (dialog, which) -> {
                                                         deleteProduct();
                                                     })
                                                     .setNegativeButton("No", (dialog, which) -> {
                                                         dialog.dismiss();
                                                     })
                                                     .show();
        });
    }

    private void deleteProduct() {
        if (product != null) {
            new Thread(() -> {
                database.productDao()
                        .deleteProduct(product);
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(getContext(), "The product has been deleted!", Toast.LENGTH_SHORT)
                         .show();
                    NavHostFragment.findNavController(this)
                                   .navigate(R.id.action_EditProductFragment_to_ListProductsFragment);
                });
            }).start();
        } else {
            Toast.makeText(getContext(), "The product cannot be deleted!", Toast.LENGTH_SHORT)
                 .show();
        }
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
        binding.editName.setText(product.getName());
        binding.editCompany.setText(product.getCompany());
        binding.editPrice.setText(String.valueOf(product.getPrice()));
        binding.editShop.setText(product.getShop());
        binding.editCategory.setText(product.getCategory());
        binding.editNotes.setText(product.getNotes());
        binding.ratingBar.setRating(product.getRating());

        if (product.getPhotoPath() != null) {
            binding.imageProduct.setImageURI(Uri.parse(product.getPhotoPath()));
        } else {
            binding.imageProduct.setImageResource(R.drawable.ic_placeholder);
        }
    }

    private boolean validateInputs() {
        if (TextUtils.isEmpty(binding.editName.getText())) {
            Toast.makeText(getContext(), "Name cannot be null or empty", Toast.LENGTH_SHORT)
                 .show();
            return false;
        }
        return true;
    }

    private void saveChanges() {
        product.setName(binding.editName.getText()
                                        .toString());
        product.setCompany(binding.editCompany.getText()
                                              .toString());
        final String priceString = binding.editPrice.getText()
                                                    .toString();
        double price = TextUtils.isEmpty(priceString) ?
                0.0 :
                Double.parseDouble(priceString);
        product.setPrice(price);
        product.setShop(binding.editShop.getText()
                                        .toString());
        product.setCategory(binding.editCategory.getText()
                                                .toString());
        product.setNotes(binding.editNotes.getText()
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


        new Thread(() -> {
            database.productDao()
                    .updateProduct(product);
            requireActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Product updated!", Toast.LENGTH_SHORT)
                                                       .show());
        }).start();
    }

    @SuppressLint("QueryPermissionsNeeded")
    private void openCamera() {
        if (requireContext().checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA);
        } else {
            final Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
                final File photoFile = createImageFile();
                if (photoFile != null) {
                    photoUri = FileProvider.getUriForFile(requireContext(), requireActivity().getPackageName() + ".fileprovider", photoFile);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                    takePictureLauncher.launch(takePictureIntent);
                }
            }
        }
    }

    @SuppressLint("QueryPermissionsNeeded")
    private void openGallery() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (requireContext().checkSelfPermission(Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES);
            } else {
                launchGalleryIntent();
            }
        } else {
            if (requireContext().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
            } else {
                launchGalleryIntent();
            }
        }
    }

    private void launchGalleryIntent() {
        final Intent pickPhotoIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickFromGalleryLauncher.launch(pickPhotoIntent);
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
