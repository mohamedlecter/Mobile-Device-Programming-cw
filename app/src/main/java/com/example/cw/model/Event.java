package com.example.cw.model;
import java.io.Serializable;

public class Event implements Serializable {
    private String title;
    private String location;
    private String description;
    private String date;
    private String eventImg;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location)
    {
        this.location = location;
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

    public String getImagePath() {
        return eventImg;
    }

    public void setImagePath(String imagePath) {
        this.eventImg = imagePath;
    }

}
