package com.example.folbeta;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        // Setup the Toolbar with FOL Clothing and logo
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("FOL Clothing");
        // You can also set an icon/logo here if needed:
        // toolbar.setLogo(R.drawable.logo); // Assuming you have a logo file

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        productList = new ArrayList<>();
        productAdapter = new ProductAdapter(productList, this);
        recyclerView.setAdapter(productAdapter);

        btnGoToCart = findViewById(R.id.btn_go_to_cart);
        btnGoToCart.setOnClickListener(v -> {
            Intent intent = new Intent(ProductActivity.this, CartActivity.class);
            startActivity(intent);
        });

        loadProducts();
    }

    private void loadProducts() {
        new GetProductsTask().execute();
    }

    private class GetProductsTask extends AsyncTask<Void, Void, String> {
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
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s != null) {
                try {
                    JSONArray jsonArray = new JSONArray(s);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject obj = jsonArray.getJSONObject(i);
                        productList.add(new Product(
                                obj.getInt("id"),
                                obj.getString("name"),
                                obj.getDouble("price"),
                                obj.getString("colors"),
                                obj.getString("sizes"),
                                obj.getString("category"),
                                obj.getString("image_url")
                        ));
                    }
                    productAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    Toast.makeText(ProductActivity.this, "Error parsing JSON!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu); // Inflate the extended menu
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.add_to_cart) {

                // Redirect to CartActivity
                Intent cartIntent = new Intent(ProductActivity.this, CartActivity.class);
                startActivity(cartIntent);
                return true;}
        if(item.getItemId()==R.id.admin_panel){

                // Redirect to LoginActivity
                Intent loginIntent = new Intent(ProductActivity.this, LoginActivity.class);
                startActivity(loginIntent);
                return true;}
            else{
                return super.onOptionsItemSelected(item);
        }
    }
}
