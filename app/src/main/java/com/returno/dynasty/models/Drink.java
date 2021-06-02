package com.returno.dynasty.models;

public class Drink {
    private String drinkName;
    private int previousPrice;
    private int currentPrice;
    private String itemUrl ;
    private String drinkCategory;

    public Drink(String drinkName, int previousPrice, int currentPrice, String itemUrl, String drinkCategory) {
        this.drinkName = drinkName;
        this.previousPrice = previousPrice;
        this.currentPrice = currentPrice;
        this.itemUrl = itemUrl;
        this.drinkCategory = drinkCategory;
    }

    public String getDrinkName() {
        return drinkName;
    }

    public int getPreviousPrice() {
        return previousPrice;
    }

    public int getCurrentPrice() {
        return currentPrice;
    }

    public String getItemUrl() {
        return itemUrl;
    }

    public String getDrinkCategory() {
        return drinkCategory;
    }
}
