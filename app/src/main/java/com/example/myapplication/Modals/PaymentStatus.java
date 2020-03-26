package com.example.myapplication.Modals;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PaymentStatus implements Comparable<PaymentStatus> {
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

    @Override
    public int compareTo(PaymentStatus o) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date start = null, end = null;
        try {
            start = sdf.parse(this.getDate());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        try {
            end = sdf.parse(o.getDate());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (start.before(end)) return -10;
        else return 10;
//        return  this.getName().compareTo(o.getName());
    }
}
