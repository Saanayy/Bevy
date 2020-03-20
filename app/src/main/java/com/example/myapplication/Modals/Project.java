package com.example.myapplication.Modals;

import java.util.Date;

public class Project {
    private String projectId;
    private String name;
    private Date confirmationDate;
    private double amount;
    private Date deadline;
    private String result;

    public Project() {
    }

    public Project(String projectId, String name, Date confirmationDate, double amount, Date deadline, String result) {
        this.projectId = projectId;
        this.name = name;
        this.confirmationDate = confirmationDate;
        this.amount = amount;
        this.deadline = deadline;
        this.result = result;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
