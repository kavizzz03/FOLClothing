package com.example.folbeta;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {
    private List<Item> itemList;
    private List<Item> itemListFull;  // A copy of the original item list for filtering
    private Context context;
    private ExecutorService executorService;
    private Handler mainHandler;

    public ItemAdapter(List<Item> itemList, Context context) {
        this.itemList = itemList;
        this.itemListFull = new ArrayList<>(itemList); // Store a copy for filtering
        this.context = context;
        this.executorService = Executors.newSingleThreadExecutor();
        this.mainHandler = new Handler(Looper.getMainLooper());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Item item = itemList.get(position);

        holder.tvName.setText("Name: " + item.getName());
        holder.tvPrice.setText("Price: LKR " + item.getPrice());
        holder.tvColors.setText("Colors: " + item.getColors());
        holder.tvSizes.setText("Sizes: " + item.getSizes());
        holder.tvCategory.setText("Category: " + item.getCategory());

        Glide.with(context)
                .load(item.getImageUrl()) // Ensure this URL is correct and points to an image
                .into(holder.imgItem);

        holder.btnDelete.setOnClickListener(v -> deleteItem(item.getId(), position));

        holder.btnUpdate.setOnClickListener(v -> {
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

    public static class ViewHolder extends RecyclerView.ViewHolder {
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
        executorService.execute(() -> {
            try {
                URL url = new URL("https://modaloku.cpsharetxt.com/delete_item.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);

                OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
                writer.write("id=" + id);
                writer.flush();
                writer.close();

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder result = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
                reader.close();

                // Check if the result is success
                mainHandler.post(() -> {
                    String response = result.toString().trim();
                    if (response.equals("Item deleted successfully!")) {
                        itemList.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, itemList.size());
                        Toast.makeText(context, response, Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(context, "Failed to delete item", Toast.LENGTH_LONG).show();
                    }
                });
            } catch (Exception e) {
                mainHandler.post(() -> Toast.makeText(context, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }
        });
    }

    public void filterByCategory(String category) {
        if (category.equals("Men")) {
            itemList.clear();
            for (Item item : itemListFull) {
                if (item.getCategory().equals("Men")) {
                    itemList.add(item);
                }
            }
        } else if (category.equals("Women")) {
            itemList.clear();
            for (Item item : itemListFull) {
                if (item.getCategory().equals("Women")) {
                    itemList.add(item);
                }
            }
        } else if (category.equals("Kids")) {
            itemList.clear();
            for (Item item : itemListFull) {
                if (item.getCategory().equals("Kids")) {
                    itemList.add(item);
                }
            }
        } else {
            // If no category is selected, show all items
            itemList = new ArrayList<>(itemListFull);
        }
        notifyDataSetChanged();
    }
}
