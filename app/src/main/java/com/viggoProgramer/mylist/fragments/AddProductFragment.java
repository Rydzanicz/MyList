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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.viggoProgramer.mylist.R;
import com.viggoProgramer.mylist.ads.AdManager;
import com.viggoProgramer.mylist.databinding.FragmentAddProductBinding;
import com.viggoProgramer.mylist.product.AppDatabase;
import com.viggoProgramer.mylist.product.Product;
import com.viggoProgramer.mylist.product.ShopTag;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AddProductFragment extends Fragment {

    private FragmentAddProductBinding binding;
    private ActivityResultLauncher<Intent> takePictureLauncher;
    private ActivityResultLauncher<Intent> pickFromGalleryLauncher;
    private Uri photoUri;
    private static final List<Product> productList = new ArrayList<>();
    private AppDatabase database;
    private ActivityResultLauncher<String> requestPermissionLauncher;
    private ChipGroup chipGroupShopTags;
    private List<String> selectedTags;

    @Override
    public void onCreate(final @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        selectedTags            = new ArrayList<>();
    }

    @Override
    public View onCreateView(final @NonNull LayoutInflater inflater,
                             final ViewGroup container,
                             final Bundle savedInstanceState) {
        binding = FragmentAddProductBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(final @NonNull View view,
                              final @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        database = AppDatabase.getInstance(requireContext());
        AdManager.loadInterstitialAd(requireContext());

        binding.buttonAddPhotoCamera.setOnClickListener(v -> openCamera());
        binding.buttonAddPhotoGallery.setOnClickListener(v -> openGallery());
        binding.buttonCancel.setOnClickListener(v -> AdManager.showInterstitialAd(requireContext(), () -> {
            NavHostFragment.findNavController(this)
                           .navigate(R.id.action_AddProductFragment_to_ListProductsFragment);
        }));
        binding.buttonSave.setOnClickListener(v -> {
            if (validateInputs()) {
                saveProduct();
                AdManager.showInterstitialAd(requireContext(), () -> {
                    NavHostFragment.findNavController(this)
                                   .navigate(R.id.action_AddProductFragment_to_ListProductsFragment);
                });
            }
        });

        chipGroupShopTags = view.findViewById(R.id.chipGroupShopTags);
        database          = AppDatabase.getInstance(requireContext());

        loadShopTags();

        view.findViewById(R.id.buttonAddTags)
            .setOnClickListener(v -> showAddTagDialog());
    }

    @SuppressLint("QueryPermissionsNeeded")
    private void openCamera() {
        if (requireContext().checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA);
        } else {
            final Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
                File photoFile = createImageFile();
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
        final @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        final String imageFileName = "JPEG_" + timeStamp + "_";
        final File storageDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = null;
        try {
            image = File.createTempFile(imageFileName, ".jpg", storageDir);
        } catch (IOException e) {
            Toast.makeText(getContext(), "Error creating file: " + e.getMessage(), Toast.LENGTH_SHORT)
                 .show();
        }
        return image;
    }

    private void saveProduct() {
        final String company = binding.editCompany.getText()
                                                  .toString();
        final String name = binding.editName.getText()
                                            .toString();
        final List<String> shop = new ArrayList<>(selectedTags);

        final String priceString = binding.editPrice.getText()
                                                    .toString();
        double price = TextUtils.isEmpty(priceString) ?
                0.0 :
                Double.parseDouble(priceString);
        final float rating = binding.ratingBar.getRating();
        final String notes = binding.editNotes.getText()
                                              .toString();
        final String category = binding.editCategory.getText()
                                                    .toString();

        String photoPath = null;

        if (photoUri != null) {
            try {
                photoPath = saveImageToAppFolder(photoUri);
            } catch (IOException e) {
                Toast.makeText(getContext(), "Failed to save photo", Toast.LENGTH_SHORT)
                     .show();
                e.printStackTrace();
            }
        }

        final Product product = new Product(company, name, shop, price, rating, notes, photoPath, category);

        new Thread(() -> {
            database.productDao()
                    .insertProduct(product);
            requireActivity().runOnUiThread(() -> {
                Toast.makeText(getContext(), "Product saved to database!", Toast.LENGTH_SHORT)
                     .show();
            });
        }).start();
    }

    private String saveImageToAppFolder(final Uri imageUri) throws IOException {
        final InputStream inputStream = requireContext().getContentResolver()
                                                        .openInputStream(imageUri);

        final File appFolder = new File(requireContext().getFilesDir(), "images");
        if (!appFolder.exists()) {
            appFolder.mkdirs();
        }

        final String fileName = "IMG_" + System.currentTimeMillis() + ".jpg";
        final File photoFile = new File(appFolder, fileName);

        final OutputStream outputStream = new FileOutputStream(photoFile);
        final byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) > 0) {
            outputStream.write(buffer, 0, length);
        }

        outputStream.close();
        inputStream.close();

        return photoFile.getAbsolutePath();
    }

    private boolean validateInputs() {
        if (TextUtils.isEmpty(binding.editName.getText())) {
            Toast.makeText(getContext(), "Name cannot be null or empty", Toast.LENGTH_SHORT)
                 .show();
            return false;
        }
        return true;
    }

    private void loadShopTags() {
        new Thread(() -> {
            final List<ShopTag> shopTags = database.shopTagDao()
                                                   .getAllShopTags();
            requireActivity().runOnUiThread(() -> {
                chipGroupShopTags.removeAllViews();
                for (ShopTag tag : shopTags) {
                    loadAddChipToGroup(tag.getTagName());
                }
            });
        }).start();
    }

    private void addChipToGroup(final String tagName) {
        final Chip chip = new Chip(requireContext());
        chip.setText(tagName);
        chip.setCheckable(true);

        if (selectedTags == null) {
            selectedTags = new ArrayList<>();
        }
        chip.setChecked(true);
        selectedTags.add(tagName);

        chip.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                if (!selectedTags.contains(tagName)) {
                    selectedTags.add(tagName);
                }
            } else {
                selectedTags.remove(tagName);
            }
        });

        chipGroupShopTags.addView(chip);
    }

    private void loadAddChipToGroup(final String tagName) {
        final Chip chip = new Chip(requireContext());
        chip.setText(tagName);
        chip.setCheckable(true);
        chip.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                if (selectedTags == null) {
                    selectedTags = new ArrayList<>();
                }
                selectedTags.add(tagName);
            } else {
                if (selectedTags != null) {
                    selectedTags.remove(tagName);
                }
            }
        });
        chipGroupShopTags.addView(chip);
    }

    private void showAddTagDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle(getString(R.string.enter_shop));

        final LayoutInflater inflater = requireActivity().getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_add_tag, null);
        final EditText editTextTagName = dialogView.findViewById(R.id.editTextTagName);

        builder.setView(dialogView);

        final AlertDialog dialog = builder.create();

        final Button buttonSave = dialogView.findViewById(R.id.buttonSaveTags);
        buttonSave.setOnClickListener(v -> {
            String tagName = editTextTagName.getText().toString().trim();
            if (!TextUtils.isEmpty(tagName)) {

                saveTagToDatabase(tagName);
                dialog.dismiss();
            } else {
                Toast.makeText(requireContext(), getString(R.string.shop_name_empty), Toast.LENGTH_SHORT).show();
            }
        });

        final Button buttonCancel = dialogView.findViewById(R.id.buttonCancel);
        buttonCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void saveTagToDatabase(final String tagName) {
        new Thread(() -> {
            final ShopTag newTag = new ShopTag(tagName);
            database.shopTagDao()
                    .insertShopTag(newTag);
            requireActivity().runOnUiThread(() -> {
                addChipToGroup(tagName);
                Toast.makeText(requireContext(), getString(R.string.enter_shop), Toast.LENGTH_SHORT)
                     .show();
            });
        }).start();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
