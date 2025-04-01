package com.example.folbeta;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class CardPaymentActivity extends AppCompatActivity {
    private EditText emailInput, addressInput, cardNumberInput, expiryDateInput, cvvInput;
    private Button submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_payment);

        emailInput = findViewById(R.id.email_input);
        addressInput = findViewById(R.id.address_input);
        cardNumberInput = findViewById(R.id.card_number_input);
        expiryDateInput = findViewById(R.id.expiry_date_input);
        cvvInput = findViewById(R.id.cvv_input);
        submitButton = findViewById(R.id.submit_button);

        submitButton.setOnClickListener(v -> submitPayment());
    }

    private void submitPayment() {
        String email = emailInput.getText().toString().trim();
        String address = addressInput.getText().toString().trim();
        String cardNumber = cardNumberInput.getText().toString().trim();
        String expiryDate = expiryDateInput.getText().toString().trim();
        String cvv = cvvInput.getText().toString().trim();

        if (email.isEmpty() || address.isEmpty() || cardNumber.isEmpty() || expiryDate.isEmpty() || cvv.isEmpty()) {
            Toast.makeText(this, "Please fill all fields!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Here, simulate the payment processing and then send the email
        sendEmail(email, address);
    }

    private void sendEmail(String email, String address) {
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
                String postData = "email=" + email + "&subject=Order Confirmation &message=" +
                        "Your payment has been successfully processed.\n" +
                        "Your order has been placed and will be delivered to the following address:\n\n" +
                        "Address: " + address + "\n\n" +
                        "Thank you for shopping with us. You will receive your order within 3 days.";

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
                            Toast.makeText(this, "Payment successful. Order confirmed!", Toast.LENGTH_SHORT).show();
                            // Navigate to ProductActivity after email is sent
                            Intent intent = new Intent(this, ProductActivity.class);
                            startActivity(intent);
                            finish(); // Optional: Call finish to close this activity
                        });
                    } else {
                        runOnUiThread(() -> Toast.makeText(this, "Failed to send order confirmation.", Toast.LENGTH_SHORT).show());
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
