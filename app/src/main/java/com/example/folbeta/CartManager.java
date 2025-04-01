package com.example.folbeta;

import android.util.Log;
import java.util.ArrayList;
import java.util.List;

public class CartManager {
    private static CartManager instance;
    private List<Product> cartItems;

    private CartManager() {
        cartItems = new ArrayList<>();
    }

    public static synchronized CartManager getInstance() {
        if (instance == null) {
            instance = new CartManager();
        }
        return instance;
    }

    public void addToCart(Product product) {
        cartItems.add(product);
        Log.d("CartDebug", "Product added to cart: " + product.getName());  // Debug log
        Log.d("CartDebug", "Total items in cart: " + cartItems.size());
    }

    public List<Product> getCartItems() {
        Log.d("CartDebug", "Retrieving cart items. Total: " + cartItems.size());
        return cartItems;
    }
}
