package com.example.folbeta;

public class Product {
    private int id;
    private String name;
    private double price;
    private String colors;
    private String sizes;
    private String category;
    private String imageUrl;
    private boolean isSelected;
    private int quantity;

    public Product(int id, String name, double price, String colors, String sizes, String category, String imageUrl) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.colors = colors;
        this.sizes = sizes;
        this.category = category;
        this.imageUrl = imageUrl;
        this.isSelected = false;
        this.quantity = 1; // Default quantity set to 1
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public double getPrice() { return price; }
    public String getColors() { return colors; }
    public String getSizes() { return sizes; }
    public String getCategory() { return category; }
    public String getImageUrl() { return imageUrl; }
    public boolean isSelected() { return isSelected; }
    public void setSelected(boolean selected) { isSelected = selected; }

    public int getQuantity() { return quantity; }

    public void setQuantity(int quantity) {
        if (quantity > 0) {
            this.quantity = quantity;
        }
    }

    public void increaseQuantity() {
        this.quantity++;
    }

    public void decreaseQuantity() {
        if (this.quantity > 1) {
            this.quantity--;
        }
    }
}