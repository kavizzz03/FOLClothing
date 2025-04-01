package com.example.folbeta;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Button btnAddItems = findViewById(R.id.btn_add_items);
        Button btnViewUpdate = findViewById(R.id.btn_view_update);
        Button btnViewWebsite = findViewById(R.id.btn_view_website);

        btnAddItems.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, AddItemActivitys.class); // Updated Activity Name
            startActivity(intent);
        });

        btnViewUpdate.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, ViewUpdateActivity.class);
            startActivity(intent);
        });

        btnViewWebsite.setOnClickListener(v -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://yourwebsite.com"));
            startActivity(browserIntent);
        });
    }

    // Add options menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_options, menu); // Ensure 'menu_options.xml' exists in 'res/menu'
        return true;
    }

    // Handle menu item clicks
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.action_sign_out) {

            Toast.makeText(this, "Signed Out", Toast.LENGTH_SHORT).show();
            finish(); // Close activity
            return true;
        }
        if(item.getItemId()==R.id.action_go_back) {
            onBackPressed();
                return true;
        }
        if(item.getItemId()==R.id.action_view_website) {

            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://yourwebsite.com"));
            startActivity(browserIntent);
            return true;
        }
        else {

                return super.onOptionsItemSelected(item);
        }
    }
}
