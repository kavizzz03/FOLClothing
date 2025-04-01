package com.example.folbeta;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends Activity {

    private static final int SPLASH_TIME_OUT = 3000; // 3 seconds delay

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find Views
        ImageView imgLogo = findViewById(R.id.img_fol_logo);
        TextView txtWelcome = findViewById(R.id.txt_welcome);

        // Delay and Open ProductsActivity
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(MainActivity.this, ProductActivity.class);
            startActivity(intent);
            finish(); // Close this activity
        }, SPLASH_TIME_OUT);
    }
}
