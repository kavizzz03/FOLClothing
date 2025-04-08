package com.example.folbeta;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ViewUpdateActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ItemAdapter itemAdapter;
    private List<Item> itemList = new ArrayList<>();
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_update);

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        itemAdapter = new ItemAdapter(itemList, this);
        recyclerView.setAdapter(itemAdapter);

        // Swipe-to-refresh functionality
        swipeRefreshLayout.setOnRefreshListener(() -> loadItems());

        // Load items from server
        loadItems();
    }

    private void loadItems() {
        if (isNetworkAvailable()) {
            swipeRefreshLayout.setRefreshing(true); // Show loading spinner while refreshing
            new AsyncTask<Void, Void, String>() {
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
                        return result.toString();
                    } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }
                }

                @Override
                protected void onPostExecute(String result) {
                    swipeRefreshLayout.setRefreshing(false); // Hide loading spinner
                    if (result != null) {
                        try {
                            JSONArray jsonArray = new JSONArray(result);
                            itemList.clear(); // Clear old data before adding new data
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject obj = jsonArray.getJSONObject(i);
                                itemList.add(new Item(
                                        obj.getInt("id"),
                                        obj.getString("name"),
                                        obj.getString("price"),
                                        obj.getString("colors"),
                                        obj.getString("sizes"),
                                        obj.getString("category"),
                                        obj.getString("image_url")
                                ));
                            }
                            itemAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(ViewUpdateActivity.this, "Error parsing data.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(ViewUpdateActivity.this, "Error loading items.", Toast.LENGTH_SHORT).show();
                    }
                }
            }.execute();
        } else {
            Toast.makeText(this, "No network connection available.", Toast.LENGTH_SHORT).show();
        }
    }

    // Check network connection
    private boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    // ItemAdapter class to handle display of item data
    public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {

        private List<Item> itemList;
        private Context context;

        public ItemAdapter(List<Item> itemList, Context context) {
            this.itemList = itemList;
            this.context = context;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Item item = itemList.get(position);

            holder.tvName.setText("Name: " + item.getName());
            holder.tvPrice.setText("Price: LKR " + item.getPrice());
            holder.tvColors.setText("Colors: " + item.getColors());
            holder.tvSizes.setText("Sizes: " + item.getSizes());
            holder.tvCategory.setText("Category: " + item.getCategory());

            // Ensure the image URL is complete (relative URL)
            String imageUrl = "https://modaloku.cpsharetxt.com/" + item.getImageUrl(); // Assuming the image path is relative

            // Use Glide to load image with error handling
            Glide.with(context)
                    .load(imageUrl)
                    .apply(new RequestOptions().error(R.drawable.default_image))  // Placeholder for error
                    .into(holder.imgItem);

            // Handle Delete Button Click
            holder.btnDelete.setOnClickListener(v -> {
                Log.d("ItemAdapter", "Delete button clicked for item ID: " + item.getId());
                deleteItem(item.getId(), position);
            });

            // Handle Update Button Click
            holder.btnUpdate.setOnClickListener(v -> {
                Log.d("ItemAdapter", "Update button clicked for item ID: " + item.getId());
                // Pass the item data to UpdateActivity via intent
                Intent intent = new Intent(context, UpdateActivity.class);
                intent.putExtra("item_id", item.getId());
                intent.putExtra("item_name", item.getName());
                intent.putExtra("item_price", item.getPrice());
                intent.putExtra("item_colors", item.getColors());
                intent.putExtra("item_sizes", item.getSizes());
                intent.putExtra("item_category", item.getCategory());
                intent.putExtra("item_image_url", item.getImageUrl());
                context.startActivity(intent);
            });
        }

        @Override
        public int getItemCount() {
            return itemList.size();
        }

        // ViewHolder for each item
        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView tvName, tvPrice, tvColors, tvSizes, tvCategory;
            ImageView imgItem;
            Button btnDelete, btnUpdate;

            public ViewHolder(View itemView) {
                super(itemView);
                tvName = itemView.findViewById(R.id.tv_name);
                tvPrice = itemView.findViewById(R.id.tv_price);
                tvColors = itemView.findViewById(R.id.tv_colors);
                tvSizes = itemView.findViewById(R.id.tv_sizes);
                tvCategory = itemView.findViewById(R.id.tv_category);
                imgItem = itemView.findViewById(R.id.img_item);
                btnDelete = itemView.findViewById(R.id.btn_delete);
                btnUpdate = itemView.findViewById(R.id.btn_update);
            }
        }

        private void deleteItem(int id, int position) {
            Log.d("ItemAdapter", "Attempting to delete item with ID: " + id);
            new AsyncTask<Void, Void, String>() {
                @Override
                protected String doInBackground(Void... voids) {
                    try {
                        URL url = new URL("https://modaloku.cpsharetxt.com/delete_item.php");
                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        conn.setRequestMethod("POST");
                        conn.setDoOutput(true);

                        // Send item ID to server
                        conn.getOutputStream().write(("id=" + id).getBytes());

                        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                        StringBuilder result = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            result.append(line);
                        }

                        return result.toString();
                    } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }
                }

                @Override
                protected void onPostExecute(String result) {
                    if (result != null && result.trim().equals("Item deleted successfully!")) {
                        itemList.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, itemList.size());
                        Toast.makeText(context, "Item deleted successfully!", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(context, "Error deleting item.", Toast.LENGTH_LONG).show();
                    }
                }
            }.execute();
        }
    }
}
