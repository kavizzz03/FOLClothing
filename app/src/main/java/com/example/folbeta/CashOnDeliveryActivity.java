package com.example.folbeta;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class CashOnDeliveryActivity extends AppCompatActivity {
    private EditText emailInput, contactNumberInput, addressInput, hometownInput;
    private Spinner provinceSpinner;
    private Button submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cash_on_delivery);

        emailInput = findViewById(R.id.email_input);
        contactNumberInput = findViewById(R.id.contact_number_input);
        addressInput = findViewById(R.id.address_input);
        hometownInput = findViewById(R.id.hometown_input);
        provinceSpinner = findViewById(R.id.province_spinner);
        submitButton = findViewById(R.id.submit_button);

        // Set up the Spinner with the provinces
        String[] provinces = new String[]{"Western", "Southern", "Central", "Northern", "Eastern", "Uva", "North Western", "North Central", "Sabaragamuwa"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, provinces);
        provinceSpinner.setAdapter(adapter);

        submitButton.setOnClickListener(v -> submitOrder());
    }

    private void submitOrder() {
        String email = emailInput.getText().toString().trim();
        String contactNumber = contactNumberInput.getText().toString().trim();
        String address = addressInput.getText().toString().trim();
        String hometown = hometownInput.getText().toString().trim();
        String province = provinceSpinner.getSelectedItem().toString().trim();

        if (email.isEmpty() || contactNumber.isEmpty() || address.isEmpty() || hometown.isEmpty() || province.isEmpty()) {
            Toast.makeText(this, "Please fill all the fields!", Toast.LENGTH_SHORT).show();
            return;
        }

        sendEmail(email, contactNumber, address, hometown, province);
    }

    private void sendEmail(String email, String contactNumber, String address, String hometown, String province) {
        new Thread(() -> {
            try {
                String apiUrl = "https://modaloku.cpsharetxt.com/deposite_email.php"; // Change to your actual PHP server URL
                URL url = new URL(apiUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                // Set request method to POST
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setDoInput(true);

                // Prepare the POST data
                String postData = "email=" + email + "&subject=Order Confirmation&message=" +
                        "Your order has been successfully placed. Here are the details:\n\n" +
                        "Email: " + email + "\n" +
                        "Contact Number: " + contactNumber + "\n" +
                        "Address: " + address + "\n" +
                        "Hometown: " + hometown + "\n" +
                        "Province: " + province + "\n\n" +
                        "Please ensure the payment is ready. You will receive your order within 3 days.";

                // Write the data to the connection
                try (DataOutputStream writer = new DataOutputStream(connection.getOutputStream())) {
                    writer.writeBytes(postData);
                    writer.flush();
                }

                // Read the response
                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String inputLine;
                    StringBuilder response = new StringBuilder();
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();

                    // Handle success or failure based on the response from the PHP script
                    if (response.toString().equals("success")) {
                        runOnUiThread(() -> {
                            Toast.makeText(this, "Order placed successfully!", Toast.LENGTH_SHORT).show();
                            // Go to the next activity (e.g., ProductActivity)
                            startActivity(new Intent(this, ProductActivity.class));
                        });
                    } else {
                        runOnUiThread(() -> Toast.makeText(this, "Failed to place order. Please try again.", Toast.LENGTH_SHORT).show());
                    }
                } else {
                    runOnUiThread(() -> Toast.makeText(this, "Error: " + responseCode, Toast.LENGTH_SHORT).show());
                }

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(this, "Error sending email", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }
}
