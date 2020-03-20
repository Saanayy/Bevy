package com.example.myapplication.Modals;

public class Note {
    String noteId;
    String heading;
    String description;

    public Note() {
    }

    public Note(String noteId, String heading, String description) {
        this.noteId = noteId;
        this.heading = heading;
        this.description = description;
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
}
