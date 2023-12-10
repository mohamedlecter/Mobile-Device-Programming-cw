package com.example.cw.events;

import static com.example.cw.EventDateFormatUtils.convertDateTimeToMillis;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cw.R;
import com.example.cw.model.Event;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class EventDetailsActivity extends AppCompatActivity {
    private static final int MY_PERMISSIONS_REQUEST_WRITE_CALENDAR = 123;
    private Event event; // Initialize the 'event' variable
    private long startTimeMillis;
    private long endTimeMillis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("event")) {
            event = (Event) intent.getSerializableExtra("event");

            // Retrieve TextViews from layout
            TextView eventNameTextView = findViewById(R.id.eventNameTextView);
            TextView eventDateTextView = findViewById(R.id.eventDateTextView);
            TextView eventLocationTextView = findViewById(R.id.eventLocationTextView);
            TextView eventOrganizerNameTextView = findViewById(R.id.eventOrganizerNameTextView);
            TextView eventDescTextView = findViewById(R.id.eventDescTextView);
            ImageView eventImageView = findViewById(R.id.eventImage);

//            // Format and display date, day, month, and time
//            String formattedYear = String.valueOf(event.getYear());
//            String formattedDay = String.valueOf(event.getDay());
//            String formattedMonth = event.getMonth();

//            String displayDateTime = String.format("%s, %s %s", formattedDay, formattedMonth, formattedYear);

            // Set event details in the TextViews
            eventNameTextView.setText(event.getTitle());
            eventDateTextView.setText(event.getStartDate());
            eventLocationTextView.setText(event.getLocation());
            eventOrganizerNameTextView.setText(event.getEventOrganizer());
            eventDescTextView.setText(event.getDescription());

            eventLocationTextView.setText(event.getLocation());

            // Convert start and end times to milliseconds
            startTimeMillis = convertDateTimeToMillis(event.getStartDate(), event.getEventTimeStart());
            endTimeMillis = convertDateTimeToMillis(event.getFinishDate(), event.getEventTimeEnd());
            Log.d("event detail", "start time" + String.valueOf(startTimeMillis));
            Log.d("Event detail", "" + String.valueOf(endTimeMillis));

            try {
//                http://:4000/uploads/1700915869210ICN.jpg
                String serverBaseUrl = "https://mdp-server-07db49d63c9e.herokuapp.com";
//                    String serverBaseUrl = "http://localhost:4000";
                String imagePath = serverBaseUrl + "/" + event.getImagePath().replace("\\", "/");
                Log.d("ImagePath 2", imagePath);
                Picasso.get()
                        .load(imagePath)
                        .into(eventImageView, new Callback() {
                            @Override
                            public void onSuccess() {
                                Log.d("Picasso", "Image loaded successfully");
                            }

                            @Override
                            public void onError(Exception e) {
                                Log.e("Picasso", "Error loading image: " + e.getMessage());
                            }
                        });

            } catch (IllegalArgumentException e) {
                // Log an error or provide a placeholder image if needed
                Log.e("EventAdapter", "Failed to load image: " + e.getMessage());
            }

            ImageView shareButton = findViewById(R.id.buttonShare);
            shareButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");

                    String shareText = String.format("Check out this event: %s on %s at %s",
                            event.getTitle(), event.getStartDate(), event.getLocation());

                    shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);

                    startActivity(Intent.createChooser(shareIntent, "Share Event"));

                }
            });



            // Button for adding the event to the calendar
            ImageView addToCalendarButton = findViewById(R.id.buttonCalender);
            addToCalendarButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        // Check if the app has the calendar write permission
                        if (ContextCompat.checkSelfPermission(EventDetailsActivity.this,
                                Manifest.permission.WRITE_CALENDAR)
                                != PackageManager.PERMISSION_GRANTED) {
                            // Permission is not granted, request it
                            ActivityCompat.requestPermissions(EventDetailsActivity.this,
                                    new String[]{Manifest.permission.WRITE_CALENDAR},
                                    MY_PERMISSIONS_REQUEST_WRITE_CALENDAR);
                        } else {
                            // Permission already granted, add the event to the calendar
                            addToCalendar();
                        }
                    } else {
                        // For versions lower than Marshmallow, no runtime permission required
                        addToCalendar();
                    }
                }
            });

            // Button for redirecting to the home page
            ImageView backButton = findViewById(R.id.buttonBackToHome);
            backButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Create an intent to go back to the home page
                    Intent homeIntent = new Intent(EventDetailsActivity.this, HomeActivity.class);
                    startActivity(homeIntent);
                }
            });

            Button purchaseButton = findViewById(R.id.purchaseButton);
            purchaseButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    redirectToGoogleForm();
                }
            });
        }
    }

    private void redirectToGoogleForm() {
        String googleFormUrl = "https://docs.google.com/forms/d/1SIpYePRN1i3DXfo6wizDifV3dalAL2J6OWRlPQwR6kk";

        // Create an Intent with the ACTION_VIEW action and the Google Form URL
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(googleFormUrl));

        // Verify that there is a browser app available to handle the intent
        PackageManager packageManager = getPackageManager();
        List<ResolveInfo> browserActivities = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);

        if (!browserActivities.isEmpty()) {
            // If there is a browser app available, start the intent
            startActivity(intent);
        } else {
            // If no browser app is found, you can handle this case (e.g., show an error message)
            Toast.makeText(this, "No web browser found", Toast.LENGTH_SHORT).show();
        }
    }


    private void addToCalendar() {
        Intent intent = new Intent(Intent.ACTION_INSERT);
        intent.setData(CalendarContract.Events.CONTENT_URI);
        intent.putExtra(CalendarContract.Events.TITLE, event.getTitle());
        intent.putExtra(CalendarContract.Events.EVENT_LOCATION, event.getLocation());
        intent.putExtra(CalendarContract.Events.DESCRIPTION, event.getDescription());

        // Set the event start and end time
        intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startTimeMillis);
        intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTimeMillis);

        // Check if the calendar app is available on the device
        startActivity(intent);

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults); // Call super method
        if (requestCode == MY_PERMISSIONS_REQUEST_WRITE_CALENDAR) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, add the event to the calendar
                addToCalendar();
            } else {
                // Permission denied, handle accordingly
                Toast.makeText(this, "Calendar write permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }


}