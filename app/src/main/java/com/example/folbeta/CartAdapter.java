package com.example.folbeta;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import java.util.List;

public class CartAdapter extends BaseAdapter {
    private Context context;
    private List<Product> cartItems;
    private CartActivity cartActivity;

    public CartAdapter(Context context, List<Product> cartItems, CartActivity cartActivity) {
        this.context = context;
        this.cartItems = cartItems;
        this.cartActivity = cartActivity;
    }

    @Override
    public int getCount() {
        return cartItems.size();
    }

    @Override
    public Object getItem(int position) {
        return cartItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false);
        }

        Product product = cartItems.get(position);
        TextView productName = convertView.findViewById(R.id.cart_product_name);
        TextView productPrice = convertView.findViewById(R.id.cart_product_price);
        ImageView productImage = convertView.findViewById(R.id.cart_product_image);
        TextView quantityText = convertView.findViewById(R.id.cart_quantity);
        Button increaseButton = convertView.findViewById(R.id.btn_increase);
        Button decreaseButton = convertView.findViewById(R.id.btn_decrease);
        Button removeButton = convertView.findViewById(R.id.btn_remove);

        productName.setText(product.getName());
        productPrice.setText("LKR " + product.getPrice());
        Picasso.get().load(product.getImageUrl()).into(productImage);
        quantityText.setText(String.valueOf(product.getQuantity()));

        increaseButton.setOnClickListener(v -> {
            product.increaseQuantity();
            quantityText.setText(String.valueOf(product.getQuantity()));
            cartActivity.updateTotalPrice();
        });

        decreaseButton.setOnClickListener(v -> {
            if (product.getQuantity() > 1) {
                product.decreaseQuantity();
                quantityText.setText(String.valueOf(product.getQuantity()));
                cartActivity.updateTotalPrice();
            }
        });

        removeButton.setOnClickListener(v -> {
            cartItems.remove(position);
            notifyDataSetChanged();
            cartActivity.updateTotalPrice();
        });

        return convertView;
    }
}