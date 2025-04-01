package com.example.folbeta;
public class Item {
    private int id;
    private String name, price, colors, sizes, category, imageUrl;

    public Item(int id, String name, String price, String colors, String sizes, String category, String imageUrl) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.colors = colors;
        this.sizes = sizes;
        this.category = category;
        this.imageUrl = imageUrl;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPrice() {
        return price;
    }

    public String getColors() {
        return colors;
    }

    public String getSizes() {
        return sizes;
    }

    public String getCategory() {
        return category;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
