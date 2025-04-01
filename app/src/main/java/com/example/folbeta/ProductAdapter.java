package com.example.folbeta;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.recyclerview.widget.RecyclerView;
import com.squareup.picasso.Picasso; // Import Picasso for image loading
import android.util.Log;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    private List<Product> productList;
    private Context context;

    public ProductAdapter(List<Product> productList, Context context) {
        this.productList = productList;
        this.context = context;
    }

    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.product_list_item, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ProductViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.tvProductName.setText(product.getName());
        holder.tvProductPrice.setText("Rs. " + product.getPrice());
        holder.tvCategory.setText("Category: " + product.getCategory());

        // ✅ Use Picasso for better image loading
        Picasso.get()
                .load(product.getImageUrl())
                .placeholder(R.drawable.ic_placeholder) // Show a placeholder if the image is loading
                .error(R.drawable.ic_placeholder) // Show a default image if the URL fails
                .into(holder.imgProduct);

        // ✅ Corrected "Add to Cart" button click logic
        holder.btnAddToCart.setOnClickListener(v -> {
            CartManager.getInstance().addToCart(product); // Add product to cart
            Toast.makeText(context, "Added to Cart!", Toast.LENGTH_SHORT).show();
            Log.d("CartDebug", "Product added: " + product.getName());  // Debug log
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProduct;
        TextView tvProductName, tvProductPrice, tvCategory;
        Button btnAddToCart;

        public ProductViewHolder(View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.product_image);
            tvProductName = itemView.findViewById(R.id.product_name);
            tvProductPrice = itemView.findViewById(R.id.product_price);
            tvCategory = itemView.findViewById(R.id.product_category);
            btnAddToCart = itemView.findViewById(R.id.btn_add_to_cart);
        }
    }
}
