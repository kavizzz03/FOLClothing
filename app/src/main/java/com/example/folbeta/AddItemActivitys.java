package com.example.folbeta;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.*;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import java.io.ByteArrayOutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class AddItemActivitys extends AppCompatActivity {

    private EditText etItemName, etPrice, etColors, etSizes;
    private Spinner spinnerCategory;
    private Button btnSave, btnSelectImage, btnBack;
    private ImageView imgItem;
    private Bitmap bitmap;
    private String encodedImage = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_items);

        etItemName = findViewById(R.id.et_item_name);
        etPrice = findViewById(R.id.et_price);
        etColors = findViewById(R.id.et_colors);
        etSizes = findViewById(R.id.et_sizes);
        spinnerCategory = findViewById(R.id.spinner_category);
        btnSave = findViewById(R.id.btn_save);
        btnSelectImage = findViewById(R.id.btn_select_image);
        imgItem = findViewById(R.id.img_item);
        btnBack = findViewById(R.id.btn_back);

        // Set onClickListener for Select Image button
        btnSelectImage.setOnClickListener(v -> openGallery());

        // Set onClickListener for Save button
        btnSave.setOnClickListener(v -> saveItem());

        // Set onClickListener for Back button
        btnBack.setOnClickListener(v -> {
            // Navigate to HomeActivity
            Intent intent = new Intent(AddItemActivitys.this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // To clear the stack and ensure HomeActivity is on top
            startActivity(intent);
            finish(); // Optional: to finish the current activity
        });
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                imgItem.setImageBitmap(bitmap);
                encodeImage(bitmap);
            } catch (Exception e) {
                Toast.makeText(this, "Error selecting image!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void encodeImage(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        encodedImage = Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    private void saveItem() {
        String name = etItemName.getText().toString().trim();
        String price = etPrice.getText().toString().trim();
        String colors = etColors.getText().toString().trim();
        String sizes = etSizes.getText().toString().trim();
        String category = spinnerCategory.getSelectedItem().toString();

        // Validate fields
        if (name.isEmpty() || price.isEmpty() || colors.isEmpty() || sizes.isEmpty() || encodedImage.isEmpty()) {
            Toast.makeText(this, "Please fill all fields and select an image!", Toast.LENGTH_SHORT).show();
            return;
        }

        new SaveItemTask().execute(name, price, colors, sizes, category, encodedImage);
    }

    private class SaveItemTask extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(AddItemActivitys.this, "Saving Item", "Please wait...", false, false);
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                String link = "https://modaloku.cpsharetxt.com/save_item.php"; // Your server URL
                String data = URLEncoder.encode("name", "UTF-8") + "=" + URLEncoder.encode(params[0], "UTF-8")
                        + "&" + URLEncoder.encode("price", "UTF-8") + "=" + URLEncoder.encode(params[1], "UTF-8")
                        + "&" + URLEncoder.encode("colors", "UTF-8") + "=" + URLEncoder.encode(params[2], "UTF-8")
                        + "&" + URLEncoder.encode("sizes", "UTF-8") + "=" + URLEncoder.encode(params[3], "UTF-8")
                        + "&" + URLEncoder.encode("category", "UTF-8") + "=" + URLEncoder.encode(params[4], "UTF-8")
                        + "&" + URLEncoder.encode("image", "UTF-8") + "=" + URLEncoder.encode(params[5], "UTF-8");

                URL url = new URL(link);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);

                OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
                writer.write(data);
                writer.flush();
                writer.close();

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder result = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }

                reader.close();
                return result.toString();
            } catch (Exception e) {
                return "Error: " + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressDialog.dismiss();
            Toast.makeText(AddItemActivitys.this, result, Toast.LENGTH_LONG).show();

            // If the item was saved successfully, clear the fields
            if (result.equals("Item saved successfully!")) {
                etItemName.setText("");
                etPrice.setText("");
                etColors.setText("");
                etSizes.setText("");
                imgItem.setImageResource(R.drawable.ic_launcher_background); // Reset the image
            }
        }
    }
}
