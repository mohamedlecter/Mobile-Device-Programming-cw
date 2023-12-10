package com.example.cw.events;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.cw.R;
import com.example.cw.SessionManager;
import com.example.cw.api.Api;
import com.example.cw.api.RetrofitClient;
import com.example.cw.model.Event;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditEvent extends AppCompatActivity {
    // Initialize EditText fields
    private EditText eventTitleEditText, eventDscEditText, eventLocationEditText;

    private TextView startDateEditText, endDateEditText;
    private ImageView backButton, editImageViewEvent;
    private Button pickImageView, saveButton;
    private Event selectedEvent;
    private SessionManager sessionManager;
    private Api api;
    private Calendar calendarStart;
    private Calendar calendarEnd;
    private static final int PICK_IMAGE_REQUEST = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_event);
        sessionManager = new SessionManager(this);
        api = RetrofitClient.getInstance().getApi();


        // Initialize  views
        backButton = findViewById(R.id.buttonBackToEvents);
        eventTitleEditText = findViewById(R.id.editEventTitle);
        eventDscEditText = findViewById(R.id.editEventDsc);
        eventLocationEditText = findViewById(R.id.editEventLocation);
        startDateEditText = findViewById(R.id.editStartDate);
        endDateEditText = findViewById(R.id.editEndDate);
        pickImageView = findViewById(R.id.editBtnPickImage);
        saveButton = findViewById(R.id.buttonSave);
        editImageViewEvent = findViewById(R.id.editImageViewEvent);


        // Retrieve the selected event from the intent
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("selectedEvent")) {
            selectedEvent = (Event) intent.getSerializableExtra("selectedEvent");

            Log.d("EditEvent", "Selected Event: " + selectedEvent);

            // Populate the EditText fields with the existing values
            if (selectedEvent != null) {
                eventTitleEditText.setText(selectedEvent.getTitle());
                eventDscEditText.setText(selectedEvent.getDescription());
                eventLocationEditText.setText(selectedEvent.getLocation());
                startDateEditText.setText(selectedEvent.getStartDate());
                endDateEditText.setText(selectedEvent.getFinishDate());

                Log.d("edit event", selectedEvent.getImagePath());
                try {
//                http://:4000/uploads/1700915869210ICN.jpg
                    String serverBaseUrl = "https://mdp-server-07db49d63c9e.herokuapp.com";
//                    String serverBaseUrl = "http://localhost:4000";
                    String imagePath = serverBaseUrl + "/" + selectedEvent.getImagePath().replace("\\", "/");
                    Log.d("ImagePath 2", imagePath);
                    Picasso.get()
                            .load(imagePath)
                            .into(editImageViewEvent, new com.squareup.picasso.Callback() {
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

            }
        }

        startDateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventUtils.showDateTimePicker(
                        EditEvent.this,
                        true,
                        startDateEditText,
                        endDateEditText,
                        calendarStart,
                        calendarEnd,
                        selectedEvent
                );
            }
        });

        endDateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventUtils.showDateTimePicker(
                        EditEvent.this,
                        false,
                        startDateEditText,
                        endDateEditText,
                        calendarStart,
                        calendarEnd,
                        selectedEvent
                );
            }
        });

        pickImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventUtils.pickImage(EditEvent.this, editImageViewEvent, selectedEvent);
            }
        });


        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Populate the Event object with data
                selectedEvent.setTitle(eventTitleEditText.getText().toString());
                selectedEvent.setDescription(eventDscEditText.getText().toString());
                selectedEvent.setLocation(eventLocationEditText.getText().toString());


                // Create a new Event object to store the updated values
                Event updatedEvent = new Event();

                // Check and update the title
                String updatedTitle = eventTitleEditText.getText().toString();
                if (!updatedTitle.equals(selectedEvent.getTitle())) {
                    updatedEvent.setTitle(updatedTitle);
                } else {
                    updatedEvent.setTitle(selectedEvent.getTitle());
                }

                // Check and update the description
                String updatedDescription = eventDscEditText.getText().toString();
                if (!updatedDescription.equals(selectedEvent.getDescription())) {
                    updatedEvent.setDescription(updatedDescription);
                } else {
                    updatedEvent.setDescription(selectedEvent.getDescription());
                }

                // Check and update the location
                String updatedLocation = eventLocationEditText.getText().toString();
                if (!updatedLocation.equals(selectedEvent.getLocation())) {
                    updatedEvent.setLocation(updatedLocation);
                } else {
                    updatedEvent.setLocation(selectedEvent.getLocation());
                }

                // Check and update the start date
                if (calendarStart != null) {
                    updatedEvent.setEventTimeStart(calendarStart.getTime().toString());
                    updatedEvent.setStartDate(selectedEvent.getStartDate());  // Retain the original start date
                } else {
                    updatedEvent.setEventTimeStart(selectedEvent.getEventTimeStart());
                    updatedEvent.setStartDate(selectedEvent.getStartDate());
                }
                // Check and update the end date
                if (calendarEnd != null) {
                    updatedEvent.setEventTimeEnd(calendarEnd.getTime().toString());
                    updatedEvent.setFinishDate(selectedEvent.getFinishDate());  // Retain the original finish date
                } else {
                    updatedEvent.setEventTimeEnd(selectedEvent.getEventTimeEnd());
                    updatedEvent.setFinishDate(selectedEvent.getFinishDate());
                }

                // Check and update the image path
                String updatedImagePath = selectedEvent.getImagePath();
                if (selectedEvent.getImagePath() != null && !updatedImagePath.equals(selectedEvent.getImagePath())) {
                    updatedEvent.setImagePath(updatedImagePath);
                } else {
                    updatedEvent.setImagePath(selectedEvent.getImagePath());
                }

                // Make the API call
                update(updatedEvent);

            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an intent to go back to the home page
                Intent homeIntent = new Intent(EditEvent.this, HomeActivity.class);
                startActivity(homeIntent);
            }
        });

    }

    private void update(Event updatedEvent) {
        String userToken = sessionManager.getUserToken();
        String authorizationHeader = "Bearer " + userToken;

        // Makes the API call for updating the event
        Call<Event> call = api.updateEvent(
                authorizationHeader,
                selectedEvent.getId(),
                createPartFromImage(updatedEvent.getImagePath()),
                createPartFromString(updatedEvent.getTitle()),
                createPartFromString(updatedEvent.getDescription()),
                createPartFromString(updatedEvent.getLocation()),
                createPartFromString(updatedEvent.getStartDate()),
                createPartFromString(updatedEvent.getFinishDate()),
                createPartFromString(updatedEvent.getEventTimeStart()),
                createPartFromString(updatedEvent.getEventTimeEnd())
        );

        call.enqueue(new Callback<Event>() {
            @Override
            public void onResponse(Call<Event> call, Response<Event> response) {
                if (response.isSuccessful()) {
                    Event updatedEvent = response.body();
                    if (updatedEvent != null) {
                        Log.d("EditEvent", "Updated Event ID: " + updatedEvent.getId());

                        Intent homeIntent = new Intent(EditEvent.this, HomeActivity.class);
                        startActivity(homeIntent);
                        finish();
                    }
                } else {
                    try {
                        String errorBody = response.errorBody().string();
                        Log.e("EditEvent", "Error response body: " + errorBody);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<Event> call, Throwable t) {
                Log.e("EditEvent", "API call failed: " + t.getMessage());
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri selectedImageUri = data.getData();
            String imagePath = EventUtils.getRealPathFromUri(EditEvent.this, selectedImageUri);

            // Update the ImageView in XML layout
            editImageViewEvent.setImageURI(selectedImageUri);

            // Set the image path to the Event object
            selectedEvent.setImagePath(imagePath);
        }
    }


    private RequestBody createPartFromString(String value) {
        return RequestBody.create(MultipartBody.FORM, value);
    }


    private MultipartBody.Part createPartFromImage(String imagePath) {
        if (imagePath != null) {
            File file = new File(imagePath);
            RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
            return MultipartBody.Part.createFormData("eventImg", file.getName(), requestFile);
        } else {
            return null;
        }
    }
}