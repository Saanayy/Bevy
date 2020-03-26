package com.example.myapplication.Modals;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ProjectStatus implements Comparable<ProjectStatus> {
    String name, date;
    int completionPercentage;
    String projectStatusKey;
    int taskCount, completeTaskCount;

    public ProjectStatus() {
    }

    public ProjectStatus(String name, String date, int completionPercentage, String projectStatusKey, int taskCount, int completeTaskCount) {
        this.name = name;
        this.date = date;
        this.completionPercentage = completionPercentage;
        this.projectStatusKey = projectStatusKey;
        this.taskCount = taskCount;
        this.completeTaskCount = completeTaskCount;
    }

    public String getProjectStatusKey() {
        return projectStatusKey;
    }

    public void setProjectStatusKey(String projectStatusKey) {
        this.projectStatusKey = projectStatusKey;
    }

    public int getTaskCount() {
        return taskCount;
    }

    public void setTaskCount(int taskCount) {
        this.taskCount = taskCount;
    }

    public int getCompleteTaskCount() {
        return completeTaskCount;
    }

    public void setCompleteTaskCount(int completeTaskCount) {
        this.completeTaskCount = completeTaskCount;
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

    @Override
    public int compareTo(ProjectStatus o) {
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
