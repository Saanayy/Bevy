package com.example.myapplication.Modals;

public class ProjectStatus {
    String name, date;
    int completionPercentage;

    public ProjectStatus() {
    }

    public ProjectStatus(String name, String date, int completionPercentage) {
        this.name = name;
        this.date = date;
        this.completionPercentage = completionPercentage;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getCompletionPercentage() {
        return completionPercentage;
    }

    public void setCompletionPercentage(int completionPercentage) {
        this.completionPercentage = completionPercentage;
    }
}
