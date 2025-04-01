package com.example.folbeta;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.io.OutputStreamWriter;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.net.HttpURLConnection;
import java.net.URL;
import android.os.Handler;
import android.os.Looper;

public class UpdateActivity extends AppCompatActivity {

    private EditText etItemName, etItemPrice, etItemColors, etItemSizes, etItemImageUrl;
    private Spinner spinnerCategory;
    private Button btnUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        // Initialize Views
        etItemName = findViewById(R.id.et_item_name);
        etItemPrice = findViewById(R.id.et_item_price);
        etItemColors = findViewById(R.id.et_item_colors);
        etItemSizes = findViewById(R.id.et_item_sizes);
        etItemImageUrl = findViewById(R.id.et_item_image_url);
        spinnerCategory = findViewById(R.id.spinner_category);
        btnUpdate = findViewById(R.id.btn_update);

        // Populate Spinner with Category Options
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.categories, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);

        // Get item data from Intent
        String itemName = getIntent().getStringExtra("item_name");
        String itemPrice = getIntent().getStringExtra("item_price");
        String itemColors = getIntent().getStringExtra("item_colors");
        String itemSizes = getIntent().getStringExtra("item_sizes");
        String itemCategory = getIntent().getStringExtra("item_category");
        String itemImageUrl = getIntent().getStringExtra("item_image_url");
        int itemId = getIntent().getIntExtra("item_id", -1);

        // Set data to the views
        etItemName.setText(itemName);
        etItemPrice.setText(itemPrice);
        etItemColors.setText(itemColors);
        etItemSizes.setText(itemSizes);
        etItemImageUrl.setText(itemImageUrl);

        // Set selected category in Spinner
        int categoryPosition = adapter.getPosition(itemCategory);
        spinnerCategory.setSelection(categoryPosition);

        // Update Button Click Listener
        btnUpdate.setOnClickListener(v -> {
            // Get updated data
            String updatedItemName = etItemName.getText().toString();
            String updatedItemPrice = etItemPrice.getText().toString();
            String updatedItemColors = etItemColors.getText().toString();
            String updatedItemSizes = etItemSizes.getText().toString();
            String updatedItemCategory = spinnerCategory.getSelectedItem().toString();
            String updatedItemImageUrl = etItemImageUrl.getText().toString();

            // Update the item in the database
            updateItemInDatabase(itemId, updatedItemName, updatedItemPrice, updatedItemColors,
                    updatedItemSizes, updatedItemCategory, updatedItemImageUrl);
        });
    }

    private void updateItemInDatabase(int itemId, String name, String price, String colors,
                                      String sizes, String category, String imageUrl) {
        new Thread(() -> {
            try {
                // Define the URL for the PHP script
                URL url = new URL("https://modaloku.cpsharetxt.com/update_item.php");

                // Open a connection to the server
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);

                // Write the POST data
                OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
                writer.write("item_id=" + itemId + "&item_name=" + name + "&item_price=" + price +
                        "&item_colors=" + colors + "&item_sizes=" + sizes + "&item_category=" +
                        category + "&item_image_url=" + imageUrl);
                writer.flush();
                writer.close();

                // Read the response from the server
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder result = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
                reader.close();

                // Handle the server response
                new Handler(Looper.getMainLooper()).post(() -> {
                    if (result.toString().trim().equals("Item updated successfully!")) {
                        Toast.makeText(UpdateActivity.this, "Item Updated Successfully", Toast.LENGTH_SHORT).show();
                        finish(); // Close the activity and return to the previous screen
                    } else {
                        Toast.makeText(UpdateActivity.this, "Update Failed: " + result.toString(), Toast.LENGTH_LONG).show();
                    }
                });

            } catch (Exception e) {
                new Handler(Looper.getMainLooper()).post(() ->
                        Toast.makeText(UpdateActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show()
                );
            }
        }).start();
    }
}
