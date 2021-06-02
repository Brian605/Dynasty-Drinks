package com.returno.dynasty.models;

public class CashBack {
    private String cashBackId,phoneNumber;
    private int amount;

    public CashBack(String cashBackId, String phoneNumber, int amount) {
        this.cashBackId = cashBackId;
        this.phoneNumber = phoneNumber;
        this.amount = amount;
    }

    public String getCashBackId() {
        return cashBackId;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public int getAmount() {
        return amount;
    }
}
