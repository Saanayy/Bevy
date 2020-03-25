package com.example.myapplication.Modals;

public class Project {
    private String projectId;
    private String name;
    private String confirmationDate;
    private double amount;
    private String deadline;
    private String result;

    public Project(String projectId, String name, String confirmationDate, double amount, String deadline, String result) {
        this.projectId = projectId;
        this.name = name;
        this.confirmationDate = confirmationDate;
        this.amount = amount;
        this.deadline = deadline;
        this.result = result;
    }

    public Project() {
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

    public String getConfirmationDate() {
        return confirmationDate;
    }

    public void setConfirmationDate(String confirmationDate) {
        this.confirmationDate = confirmationDate;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
