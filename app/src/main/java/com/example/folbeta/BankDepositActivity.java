package com.example.folbeta;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class BankDepositActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private EditText emailInput;
    private ImageView slipImage;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_deposit);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        emailInput = findViewById(R.id.email_input);
        slipImage = findViewById(R.id.slip_image);
        Button uploadSlipBtn = findViewById(R.id.upload_slip_btn);
        Button submitBtn = findViewById(R.id.submit_btn);

        uploadSlipBtn.setOnClickListener(view -> openFileChooser());
        submitBtn.setOnClickListener(view -> submitPayment());
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Bank Slip"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            slipImage.setImageURI(imageUri);
        }
    }

    private void submitPayment() {
        String email = emailInput.getText().toString().trim();
        if (email.isEmpty() || imageUri == null) {
            Toast.makeText(this, "Please enter email and upload slip!", Toast.LENGTH_SHORT).show();
            return;
        }

        sendEmailUsingAPI(email);
    }

    private void sendEmailUsingAPI(String email) {
        try {
            String apiUrl = "https://modaloku.cpsharetxt.com/deposite_email.php";
            String postData = "email=" + email + "&subject=Payment Confirmation&message=Your payment has been successfully received. Thank you! \nWe will confirm your order soon.";

            // Create a URL object
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            // Send data
            DataOutputStream writer = new DataOutputStream(connection.getOutputStream());
            writer.writeBytes(postData);
            writer.flush();
            writer.close();

            // Get response
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                if (response.toString().equals("success")) {
                    Toast.makeText(this, "Email Sent Successfully", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(this, ProductActivity.class));
                } else {
                    Toast.makeText(this, "Failed to send email", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Failed to connect to server", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error sending email", Toast.LENGTH_SHORT).show();
        }
    }
}
