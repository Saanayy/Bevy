package com.example.myapplication.Modals;

public class PaymentStatus {
    String name, amount, date;
    boolean check;
    String paymentStatusKey;

    public PaymentStatus() {
    }

    public PaymentStatus(String name, String amount, String date, boolean check, String paymentStatusKey) {
        this.name = name;
        this.amount = amount;
        this.date = date;
        this.check = check;
        this.paymentStatusKey = paymentStatusKey;
    }

    public String getPaymentStatusKey() {
        return paymentStatusKey;
    }

    public void setPaymentStatusKey(String paymentStatusKey) {
        this.paymentStatusKey = paymentStatusKey;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean isCheck() {
        return check;
    }

    public void setCheck(boolean check) {
        this.check = check;
    }
}
