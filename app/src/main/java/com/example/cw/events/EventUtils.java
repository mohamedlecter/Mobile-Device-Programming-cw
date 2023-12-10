package com.example.cw.events;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.cw.model.Event;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;


public class EventUtils {

    public static final int PICK_IMAGE_REQUEST = 1;
    public static final int STORAGE_PERMISSION_CODE = 100;
    private static final String TAG = "PERMISSION_TAG";

    public static void pickImage(Activity activity, ImageView imageView, Event selectedEvent) {
        if (checkPermission(activity)) {
            Log.d(TAG, "Permissions already granted...");
            startImagePicker(activity, imageView, selectedEvent);
        } else {
            Log.d(TAG, "Permissions were not granted, request...");
            requestPermission(activity);
        }
    }

    public static void handleImagePickerResult(
            Activity activity,
            int requestCode,
            int resultCode,
            Intent data,
            ImageView imageView,
            Event selectedEvent
    ) {
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            Uri selectedImageUri = data.getData();
            String imagePath = getRealPathFromUri(activity, selectedImageUri);

            // Update the ImageView in XML layout
            imageView.setImageURI(selectedImageUri);

            // Set the image path to the Event object
            selectedEvent.setImagePath(imagePath);
        }
    }



    private static void startImagePicker(Activity activity, ImageView imageView, Event selectedEvent) {
        Intent pickIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/*");

        Intent chooserIntent = Intent.createChooser(pickIntent, "Select Image");
        try {
            activity.startActivityForResult(chooserIntent, PICK_IMAGE_REQUEST);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static boolean checkPermission(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // Android is 11(R) or above
            return Environment.isExternalStorageManager();
        } else {
            // Android is below 11(R)
            int write = ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            int read = ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE);

            return write == PackageManager.PERMISSION_GRANTED && read == PackageManager.PERMISSION_GRANTED;
        }
    }

    public static String getRealPathFromUri(Activity activity, Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = activity.getContentResolver().query(uri, projection, null, null, null);
        if (cursor == null) return null;

        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String path = cursor.getString(column_index);
        cursor.close();

        return path;
    }

    public static void requestPermission(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // Android is 11(R) or above
            try {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
                intent.setData(uri);
                activity.startActivityForResult(intent, STORAGE_PERMISSION_CODE);
            } catch (Exception e) {
                Log.e(TAG, "requestPermission: catch", e);
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                activity.startActivityForResult(intent, STORAGE_PERMISSION_CODE);
            }
        } else {
            // Android is below 11(R)
            ActivityCompat.requestPermissions(
                    activity,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                    STORAGE_PERMISSION_CODE
            );
        }
    }

    public static void showDateTimePicker(
            Activity activity,
            boolean isStartDate,
            TextView startDateTextView,
            TextView endDateTextView,
            Calendar calendarStart,
            Calendar calendarEnd,
            Event selectedEvent
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
                                    selectedEvent.setEventTimeStart(selectedTime);
                                    selectedEvent.setStartDate(selectedDate);
                                } else {
                                    endDateTextView.setText(selectedTime + " " + selectedDate);
                                    selectedEvent.setEventTimeEnd(selectedTime);
                                    selectedEvent.setFinishDate(selectedDate);
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
