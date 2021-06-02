package com.returno.dynasty.cart;

public class Cart {
    private String itemName, itemImage;
    private int itemPrice;
    private int itemQuantity;
    private int totalPrice;
    private String category="Offers";

    public Cart(String itemName, String itemImage, int itemPrice, int itemQuantity, int totalPrice) {
        this.itemName = itemName;
        this.itemImage = itemImage;
        this.itemPrice = itemPrice;
        this.itemQuantity = itemQuantity;
        this.totalPrice = totalPrice;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCategory() {
        return category;
    }

    public void setItemQuantity(int itemQuantity) {
        this.itemQuantity = itemQuantity;
    }

    public String getItemName() {
        return itemName;
    }

    public String getItemImage() {
        return itemImage;
    }

    public int getItemPrice() {
        return itemPrice;
    }

    public int getItemQuantity() {
        return itemQuantity;
    }

    public int getTotalPrice() {
        return totalPrice;
    }
}
