package com.example.cw.model;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Event implements Serializable {
    private String title;
    private String location;
    private String description;
    private String eventImg;
    private String eventOrganizer;
    private String _id;
    private String startDate;
    private String finishDate;
    private String eventTimeStart;
    private String eventTimeEnd;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImagePath() {
        return eventImg;
    }

    public void setImagePath(String imagePath) {
        this.eventImg = imagePath;
    }

    public String getEventOrganizer() {
        return eventOrganizer;
    }

    public void setEventOrganizer(String eventOrganizer) {
        this.eventOrganizer = eventOrganizer;
    }

    public String getId() {
        return _id;
    }

    public void setId(String _id) {
        this._id = _id;
    }

    public void setEventTimeStart(String eventTimeStart) {
        this.eventTimeStart = eventTimeStart;
        setStartDateAndFinishDate(eventTimeStart, true);
    }
    public String getEventTimeStart() {
        return eventTimeStart;
    }

    public void setEventTimeEnd(String eventTimeEnd) {
        this.eventTimeEnd = eventTimeEnd;
        setStartDateAndFinishDate(eventTimeEnd, false);
    }
    public String getEventTimeEnd() {
        return eventTimeEnd;
    }

    public String getFinishDate() {
        return finishDate;
    }

    public void setFinishDate(String finishDate) {
        this.finishDate = finishDate;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    // Helper method to set start and finish dates based on the selected time
    private void setStartDateAndFinishDate(String dateTime, boolean isStartTime) {
        try {
            SimpleDateFormat inputDateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.US);
            SimpleDateFormat outputDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

            // Parse the input date format
            Date date = inputDateFormat.parse(dateTime);

            // Format the date in the desired output format
            String formattedDate = outputDateFormat.format(date);

            if (isStartTime) {
                setStartDate(formattedDate);
            } else {
                setFinishDate(formattedDate);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
