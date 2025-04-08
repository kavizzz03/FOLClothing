package com.example.folbeta;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.recyclerview.widget.RecyclerView;
import com.squareup.picasso.Picasso;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> implements Filterable {
    private List<Product> productList;
    private List<Product> productListFull;  // This is a copy of the original list to help with filtering
    private Context context;

    public ProductAdapter(List<Product> productList, Context context) {
        this.productList = productList;
        this.productListFull = new ArrayList<>(productList); // Create a full copy of the list
        this.context = context;
    }

    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate the layout for individual items in the RecyclerView
        View view = LayoutInflater.from(context).inflate(R.layout.product_list_item, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ProductViewHolder holder, int position) {
        Product product = productList.get(position);

        // Set the product details
        holder.tvProductName.setText(product.getName());
        holder.tvProductPrice.setText("Rs. " + product.getPrice());
        holder.tvCategory.setText("Category: " + product.getCategory());

        // Load product image
        String imageUrl = product.getImageUrl();

        // If the image URL is not null or empty, load it using Picasso
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Log.d("ImageLoad", "Loading image from: " + imageUrl); // Log image URL

            Picasso.get()
                    .load(imageUrl)
                    .fit()
                    .centerCrop()
                    .placeholder(R.drawable.ic_placeholder) // Placeholder image while loading
                    .error(R.drawable.ic_error) // Error image if the URL fails
                    .into(holder.imgProduct, new com.squareup.picasso.Callback() {
                        @Override
                        public void onSuccess() {
                            Log.d("ImageLoad", "✅ Image loaded successfully: " + imageUrl);
                        }

                        @Override
                        public void onError(Exception e) {
                            Log.e("ImageLoad", "❌ Failed to load image: " + imageUrl, e);
                        }
                    });
        } else {
            holder.imgProduct.setImageResource(R.drawable.ic_error); // If image URL is null or empty, set error image
        }

        // Set up the "Add to Cart" button functionality
        holder.btnAddToCart.setOnClickListener(v -> {
            CartManager.getInstance().addToCart(product); // Make sure CartManager is implemented in your app
            Toast.makeText(context, "Added to Cart!", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    @Override
    public Filter getFilter() {
        return productFilter;
    }

    // Filter to search for products by name
    private Filter productFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Product> filteredList = new ArrayList<>();

            // If the search query is empty, return the full list
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(productListFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                // Loop through the full list and add matching items to filteredList
                for (Product product : productListFull) {
                    if (product.getName().toLowerCase().contains(filterPattern)) {
                        filteredList.add(product);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;
            results.count = filteredList.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            // Update the product list and notify the adapter of changes
            productList.clear();
            if (results.values != null) {
                productList.addAll((List) results.values);
            }
            notifyDataSetChanged();
        }
    };

    // ViewHolder class to hold the UI elements for each item
    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProduct;
        TextView tvProductName, tvProductPrice, tvCategory;
        Button btnAddToCart;

        public ProductViewHolder(View itemView) {
            super(itemView);
            // Initialize UI components
            imgProduct = itemView.findViewById(R.id.product_image);
            tvProductName = itemView.findViewById(R.id.product_name);
            tvProductPrice = itemView.findViewById(R.id.product_price);
            tvCategory = itemView.findViewById(R.id.product_category);
            btnAddToCart = itemView.findViewById(R.id.btn_add_to_cart); // Corrected Button ID
        }
    }
}
