package com.example.myapplication.Modals;

public class Task {
    String name, date;
    boolean check;
    String taskKey;

    public Task() {

    }

    public Task(String name, String date, boolean check, String taskKey) {
        this.name = name;
        this.date = date;
        this.check = check;
        this.taskKey = taskKey;
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

    public boolean isCheck() {
        return check;
    }

    public void setCheck(boolean check) {
        this.check = check;
    }

    public String getTaskKey() {
        return taskKey;
    }

    public void setTaskKey(String taskKey) {
        this.taskKey = taskKey;
    }
}
