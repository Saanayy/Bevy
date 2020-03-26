package com.example.myapplication.Modals;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Task implements Comparable<Task> {
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

    @Override
    public int compareTo(Task o) {
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
