package com.example.cw.jobs;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.icu.util.Calendar;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.cw.model.Job;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class JobUtils {
    public static void showDateTimePicker(
            Activity activity,
            boolean isStartDate,
            TextView startDateTextView,
            TextView endDateTextView,
            java.util.Calendar calendarStart, // Change the type here
            java.util.Calendar calendarEnd,    // Change the type her
            Job selectedJob
    ) {
        final Calendar calendar = Calendar.getInstance();

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                activity,
                (DatePicker datePicker, int year, int month, int day) -> {
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, month);
                    calendar.set(Calendar.DAY_OF_MONTH, day);

                    // Show TimePicker after selecting the date
                    TimePickerDialog timePickerDialog = new TimePickerDialog(
                            activity,
                            (TimePicker timePicker, int hour, int minute) -> {
                                calendar.set(Calendar.HOUR_OF_DAY, hour);
                                calendar.set(Calendar.MINUTE, minute);

                                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
                                String selectedTime = sdf.format(calendar.getTime());

                                SimpleDateFormat date = new SimpleDateFormat("dd, MMM yyyy", Locale.getDefault());
                                String selectedDate = date.format(calendar.getTime());

                                // Set the selected date and time to the appropriate TextView
                                if (isStartDate) {
                                    startDateTextView.setText(selectedTime + " " + selectedDate);
                                    selectedJob.setJobHourStart(selectedTime);
                                    selectedJob.setJobDurationStart(selectedDate);
                                } else {
                                    endDateTextView.setText(selectedTime + " " + selectedDate);
                                    selectedJob.setJobHourEnd(selectedTime);
                                    selectedJob.setJobDurationEnd(selectedDate);
                                }
                            },
                            calendar.get(Calendar.HOUR_OF_DAY),
                            calendar.get(Calendar.MINUTE),
                            false
                    );
                    timePickerDialog.show();
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }
}
