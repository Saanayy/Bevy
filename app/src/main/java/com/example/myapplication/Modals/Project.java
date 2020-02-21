package com.example.myapplication.Modals;

import java.util.ArrayList;
import java.util.Date;

public class Project {
    private String Name;
    private Date confirmationDate;
    private double amount;
    private Date deadline;

    public Project(String name, Date confirmationDate, double amount, Date deadline) {
        Name = name;
        this.confirmationDate = confirmationDate;

        this.amount = amount;
        this.deadline = deadline;
    }

    public Project(String name) {
        Name = name;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public Date getConfirmationDate() {
        return confirmationDate;
    }

    public void setConfirmationDate(Date confirmationDate) {
        this.confirmationDate = confirmationDate;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Date getDeadline() {
        return deadline;
    }

    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }
}
