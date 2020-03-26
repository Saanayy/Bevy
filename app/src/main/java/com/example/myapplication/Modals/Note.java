package com.example.myapplication.Modals;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Note implements Comparable<Note> {
    String noteId;
    String heading;
    String description;
    String date;

    public Note() {
    }

    public Note(String noteId, String heading, String description, String date) {
        this.noteId = noteId;
        this.heading = heading;
        this.description = description;
        this.date = date;
    }

    public String getNoteId() {
        return noteId;
    }

    public void setNoteId(String noteId) {
        this.noteId = noteId;
    }

    public String getHeading() {
        return heading;
    }

    public void setHeading(String heading) {
        this.heading = heading;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public int compareTo(Note o) {
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
