package com.example.folbeta;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ProductActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ProductAdapter productAdapter;
    private List<Product> productList;
    private Button btnGoToCart;
    private Spinner categorySpinner;
    private SwipeRefreshLayout swipeRefreshLayout;

    private static final String TAG = "ProductActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        // Initialize SwipeRefreshLayout
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("FOL Clothing");

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        productList = new ArrayList<>();
        productAdapter = new ProductAdapter(productList, this);
        recyclerView.setAdapter(productAdapter);

        btnGoToCart = findViewById(R.id.btn_go_to_cart);
        btnGoToCart.setOnClickListener(v -> startActivity(new Intent(ProductActivity.this, CartActivity.class)));

        categorySpinner = findViewById(R.id.spinner_category);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.category_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);

        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedCategory = parent.getItemAtPosition(position).toString();
                loadProducts(selectedCategory);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        // Initialize the swipe-to-refresh action
        swipeRefreshLayout.setOnRefreshListener(() -> {
            String selectedCategory = categorySpinner.getSelectedItem().toString();
            loadProducts(selectedCategory);
        });

        loadProducts("All"); // Initial load
    }

    private void loadProducts(String categoryFilter) {
        if (!isNetworkAvailable()) {
            Toast.makeText(this, "No internet connection!", Toast.LENGTH_LONG).show();
            return;
        }
        new GetProductsTask(categoryFilter).execute();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = cm.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private class GetProductsTask extends AsyncTask<Void, Void, String> {
        private final String categoryFilter;

        public GetProductsTask(String categoryFilter) {
            this.categoryFilter = categoryFilter;
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                URL url = new URL("https://modaloku.cpsharetxt.com/get_items.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder result = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
                reader.close();
                return result.toString();
            } catch (Exception e) {
                Log.e(TAG, "Error loading products", e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            if (s != null) {
                try {
                    JSONArray jsonArray = new JSONArray(s);
                    productList.clear();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject obj = jsonArray.getJSONObject(i);
                        String category = obj.getString("category");

                        if (categoryFilter.equals("All") || category.equalsIgnoreCase(categoryFilter)) {
                            String imageUrl = "https://modaloku.cpsharetxt.com/" + obj.getString("image_url").trim();
                            productList.add(new Product(
                                    obj.getInt("id"),
                                    obj.getString("name"),
                                    obj.getDouble("price"),
                                    obj.getString("colors"),
                                    obj.getString("sizes"),
                                    category,
                                    imageUrl
                            ));
                        }
                    }
                    productAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    Toast.makeText(ProductActivity.this, "Error parsing product data", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "JSON Error", e);
                }
            } else {
                Toast.makeText(ProductActivity.this, "Failed to load products", Toast.LENGTH_SHORT).show();
            }

            // Stop the refresh animation
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.add_to_cart) {
            startActivity(new Intent(ProductActivity.this, CartActivity.class));
            return true;
        }
        if (item.getItemId() == R.id.admin_panel) {
            startActivity(new Intent(ProductActivity.this, LoginActivity.class));
            return true;
        }
        if (item.getItemId() == R.id.view_website) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://modaloku.cpsharetxt.com/"));
            startActivity(browserIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Override onBackPressed to handle back button without closing the app
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // Do not close the app, just go back to the previous activity
    }
}
