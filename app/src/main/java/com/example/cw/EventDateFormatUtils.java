package com.example.cw;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
public class EventDateFormatUtils {

    public static long convertDateTimeToMillis(String date, String time) {
        try {
            // Combine date and time strings
            String dateTimeString = date + " " + time;

            Log.d("convert", "dateTimeString: " + dateTimeString);

            // Define the format for parsing date
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd, MMM yyyy", Locale.US);
            Date dateObj = dateFormat.parse(date);

            // Define the format for parsing time
            SimpleDateFormat timeFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US);
            Date timeObj = timeFormat.parse(time);

            // Combine date and time objects
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(dateObj);
            Calendar timeCalendar = Calendar.getInstance();
            timeCalendar.setTime(timeObj);
            calendar.set(Calendar.HOUR_OF_DAY, timeCalendar.get(Calendar.HOUR_OF_DAY));
            calendar.set(Calendar.MINUTE, timeCalendar.get(Calendar.MINUTE));
            calendar.set(Calendar.SECOND, timeCalendar.get(Calendar.SECOND));

            Log.d("convert", "date: " + calendar.getTime());

            // Get the time in milliseconds
            return calendar.getTimeInMillis();
        } catch (ParseException e) {
            e.printStackTrace();
            // Handle the parse exception, return an appropriate default value, or throw it
            return -1;
        }
    }


}
