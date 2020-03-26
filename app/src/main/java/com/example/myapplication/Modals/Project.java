package com.example.myapplication.Modals;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Project implements Comparable<Project> {
    private String projectId;
    private String name;
    private String confirmationDate;
    private double amount;
    private String deadline;
    private String result;
    private double collectedAmount;

    public Project(String projectId, String name, String confirmationDate, double amount, String deadline, String result, double collectedAmount) {
        this.projectId = projectId;
        this.name = name;
        this.confirmationDate = confirmationDate;
        this.amount = amount;
        this.deadline = deadline;
        this.result = result;
        this.collectedAmount = collectedAmount;
    }

    public Project() {
    }

    public double getCollectedAmount() {
        return collectedAmount;
    }

    public void setCollectedAmount(double collectedAmount) {
        this.collectedAmount = collectedAmount;
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

    @Override
    public int compareTo(Project o) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date start = null, end = null;
        try {
            start = sdf.parse(this.getDeadline());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        try {
            end = sdf.parse(o.getDeadline());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (start.before(end)) return 0;
        else return -10;
//        return  this.getName().compareTo(o.getName());
    }
}
