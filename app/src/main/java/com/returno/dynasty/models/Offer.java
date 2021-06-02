package com.returno.dynasty.models;

import com.returno.dynasty.utils.SortParams;

import java.util.Comparator;

public class Offer implements Comparator<Offer>, Comparable<Offer> {
    private String drinkName;
    private int previousPrice;
    private int currentPrice;
    private String itemUrl ;
    private int sortParam;
    private int offerId;
    private String category="Offers";

    public Offer(String drinkName, String itemUrl, int previousPrice, int currentPrice) {
        this.drinkName = drinkName;
        this.itemUrl = itemUrl;
        this.previousPrice = previousPrice;
        this.currentPrice = currentPrice;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCategory() {
        return category;
    }

    public void setOfferId(int offerId) {
        this.offerId = offerId;
    }

    public int getOfferId() {
        return offerId;
    }

    public void setSortParam(int sortParam) {
        this.sortParam = sortParam;
    }


    public String getDrinkName() {
        return drinkName;
    }

    public String getItemUrl() {
        return itemUrl;
    }


    public int getPreviousPrice() {
        return previousPrice;
    }

    public int getCurrentPrice() {
        return currentPrice;
    }


    @Override
    public int compare(Offer o1, Offer o2) {
        switch (sortParam){
            case SortParams.PRICE_ASC:
                return Integer.compare(o2.getCurrentPrice(),o1.getCurrentPrice());
            case SortParams.PRICE_DES:
                return Integer.compare(o1.getCurrentPrice(),o2.getCurrentPrice());
            case SortParams.DISCOUNT_ASC:
                return Integer.compare(o2.getPreviousPrice()-o2.getCurrentPrice(),o1.getPreviousPrice()-o1.getCurrentPrice());
            case SortParams.DISCOUNT_DESC:
                return Integer.compare(o1.getPreviousPrice()-o1.getCurrentPrice(),o2.getPreviousPrice()-o2.getCurrentPrice());
            default:return 0;
        }
    }

    @Override
    public int compareTo(Offer o) {
        switch (sortParam){
            case SortParams.PRICE_ASC:
                return Integer.compare(o.getCurrentPrice(),getCurrentPrice());
            case SortParams.PRICE_DES:
                return Integer.compare(getCurrentPrice(),o.getCurrentPrice());
            case SortParams.DISCOUNT_ASC:
                return Integer.compare(o.getPreviousPrice()-o.getCurrentPrice(),getPreviousPrice()-getCurrentPrice());
            case SortParams.DISCOUNT_DESC:
                return Integer.compare(getPreviousPrice()-getCurrentPrice(),o.getPreviousPrice()-o.getCurrentPrice());
            default:return 0;
        }
    }
}
