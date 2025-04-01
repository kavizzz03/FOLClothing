package com.example.folbeta;

import android.os.Bundle;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.OutputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import android.content.Intent;


public class LoginActivity extends AppCompatActivity {

    EditText username, password;
    Button loginBtn;
    TextView status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        loginBtn = findViewById(R.id.loginBtn);
        status = findViewById(R.id.status);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user = username.getText().toString();
                String pass = password.getText().toString();

                if (user.equals("admin") && pass.equals("1234")) {
                    status.setText("Login Success!");
                    status.setTextColor(getResources().getColor(android.R.color.holo_green_dark));

                    // Send login details to PHP backend
                    sendLoginDetails(user);
                    // Navigate to HomeActivity
                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish();  // Close the login activity
                } else {
                    status.setText("Try Again!");
                    status.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                }

                // Clear the text fields after clicking the login button
                username.setText("");
                password.setText("");
            }
        });
    }

    private void sendLoginDetails(String user) {
        // Get current date and time
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String currentTime = sdf.format(new Date());

        // Get device model
        String deviceModel = android.os.Build.MANUFACTURER + " " + android.os.Build.MODEL;

        // Get IP address
        String ipAddress = getIPAddress();

        // Send the data to PHP backend
        String serverUrl = "https://modaloku.cpsharetxt.com/send_email_folapp.php";  // Replace with your PHP script URL

        new Thread(() -> {
            try {
                URL url = new URL(serverUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);

                // Send POST data
                String data = "username=" + user + "&device=" + deviceModel + "&ip_address=" + ipAddress + "&login_time=" + currentTime;
                OutputStream outputStream = connection.getOutputStream();
                outputStream.write(data.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();

                // Get response
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    Log.d("Response", line);
                }
                reader.close();
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
            }
        }).start();
    }

    private String getIPAddress() {
        try {
            // Get the IP address (local or public depending on the network)
            return Formatter.formatIpAddress(((android.net.wifi.WifiManager) getSystemService(WIFI_SERVICE)).getConnectionInfo().getIpAddress());
        } catch (Exception e) {
            return "Not Available";
        }
    }
}
