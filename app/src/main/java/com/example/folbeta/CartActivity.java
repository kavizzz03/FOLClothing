package com.example.folbeta;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;

public class CartActivity extends AppCompatActivity {
    private ListView listView;
    private CartAdapter cartAdapter;
    private List<Product> cartItems;
    private TextView totalPrice;
    private Button payButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        listView = findViewById(R.id.cart_list);
        totalPrice = findViewById(R.id.total_price);
        payButton = findViewById(R.id.pay_button);

        cartItems = CartManager.getInstance().getCartItems();
        cartAdapter = new CartAdapter(this, cartItems, this);
        listView.setAdapter(cartAdapter);

        updateTotalPrice();

        payButton.setOnClickListener(v -> showPaymentOptions());
    }

    public void updateTotalPrice() {
        double total = 0;
        for (Product product : cartItems) {
            total += product.getPrice() * product.getQuantity();
        }
        totalPrice.setText("Total: LKR " + total);
    }

    private void showPaymentOptions() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Payment Method")
                .setItems(new String[]{"Cash on Delivery", "Bank Deposit", "Card Payment"},
                        (dialog, which) -> {
                            if (which == 0) startActivity(new Intent(this, CashOnDeliveryActivity.class));
                            else if (which == 1) startActivity(new Intent(this, BankDepositActivity.class));
                            else startActivity(new Intent(this, CardPaymentActivity.class));
                        })
                .show();
    }
}