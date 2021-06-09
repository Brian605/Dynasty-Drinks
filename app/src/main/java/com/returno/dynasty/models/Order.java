package com.returno.dynasty.models;

public class Order {
    private String orderId, orderItems, orderPrices, orderCategories, orderStatus,orderDate;
    private int totalPrice;
    private String userId;

    public Order(String orderId, String orderItems, String orderPrices, String orderCategories, String orderStatus, String orderDate, int totalPrice) {
        this.orderId = orderId;
        this.orderItems = orderItems;
        this.orderPrices = orderPrices;
        this.orderCategories = orderCategories;
        this.orderStatus = orderStatus;
        this.orderDate = orderDate;
        this.totalPrice = totalPrice;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getOrderItems() {
        return orderItems;
    }

    public String getOrderPrices() {
        return orderPrices;
    }

    public String getOrderCategories() {
        return orderCategories;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public int getTotalPrice() {
        return totalPrice;
    }
}
