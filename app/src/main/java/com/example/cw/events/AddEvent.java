package com.example.cw.events;


import static com.example.cw.events.EventUtils.getRealPathFromUri;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.cw.R;
import com.example.cw.SessionManager;
import com.example.cw.api.Api;
import com.example.cw.api.RetrofitClient;
import com.example.cw.model.Event;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddEvent extends AppCompatActivity {
    private Api api;
    private EditText eventTitleEditText;
    private EditText eventDescEditText;
    private EditText eventLocationEditText;
    private TextView startDateTextView;
    private TextView endDateTextView;
    private Calendar calendarStart;
    private Calendar calendarEnd;
    private Event event; // Declare the Event object

    private static final int PICK_IMAGE_REQUEST = 1;

    private ImageView imageViewEventPoster;
    private ImageView backButton;
    private Button btnPickImage;
    private Button saveButton;

    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        api = RetrofitClient.getInstance().getApi(); // gets the api from the retrofit client


        initializeUI();
        setupEventHandling();
        setupTextWatchers();
    }

    private void initializeUI() {
        eventTitleEditText = findViewById(R.id.eventTitle);
        eventDescEditText = findViewById(R.id.eventDsc);
        eventLocationEditText = findViewById(R.id.eventLocation);

        startDateTextView = findViewById(R.id.startDate);
        endDateTextView = findViewById(R.id.endDate);

        imageViewEventPoster = findViewById(R.id.imageViewEventPoster);
        btnPickImage = findViewById(R.id.btnPickImage);
        backButton = findViewById(R.id.buttonBackToEvents);
        saveButton = findViewById(R.id.buttonSave);

        sessionManager = new SessionManager(this);
        calendarStart = Calendar.getInstance();
        calendarEnd = Calendar.getInstance();
        event = new Event(); // Initialize the Event object

    }

    private void setupEventHandling() {
        backButton.setOnClickListener(v -> {
            // Create an intent to go back to the home page
            Intent homeIntent = new Intent(AddEvent.this, HomeActivity.class);
            startActivity(homeIntent);
        });

        btnPickImage.setOnClickListener(view -> EventUtils.pickImage(AddEvent.this, imageViewEventPoster, event));

        startDateTextView.setOnClickListener(v -> EventUtils.showDateTimePicker(
                AddEvent.this,
                true,
                startDateTextView,
                endDateTextView,
                calendarStart,
                calendarEnd,
                event
        ));

        endDateTextView.setOnClickListener(v -> EventUtils.showDateTimePicker(
                AddEvent.this,
                false,
                startDateTextView,
                endDateTextView,
                calendarStart,
                calendarEnd,
                event
        ));

        saveButton.setOnClickListener(v -> makeApiCall());
    }

    private void setupTextWatchers() {
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int before, int count) {
                // Not used
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                int i = charSequence.hashCode();
                if (i == R.id.eventTitle) {
                    validateEventTitle(charSequence.toString());
                } else if (i == R.id.eventDsc) {
                    validateEventDescription(charSequence.toString());
                } else if (i == R.id.eventLocation) {
                    validateEventLocation(charSequence.toString());
                }
                updateSaveButtonState();
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Not used
            }
        };

        eventTitleEditText.addTextChangedListener(textWatcher);
        eventDescEditText.addTextChangedListener(textWatcher);
        eventLocationEditText.addTextChangedListener(textWatcher);

        endDateTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int before, int count) {
                // Not used
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                validateEndDate(charSequence.toString());
                updateSaveButtonState();
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Not used
            }
        });
    }

    private void makeApiCall() {
        event.setTitle(eventTitleEditText.getText().toString());
        event.setDescription(eventDescEditText.getText().toString());
        event.setLocation(eventLocationEditText.getText().toString());

        event.setEventTimeStart(calendarStart.getTime().toString());
        event.setEventTimeEnd(calendarEnd.getTime().toString());

        String userToken = sessionManager.getUserToken();
        String authorizationHeader = "Bearer " + userToken;

        // Makes the API call
        Call<Event> call = api.postEvent(authorizationHeader,
                createPartFromImage(event.getImagePath()),
                createPartFromString(event.getTitle()),
                createPartFromString(event.getDescription()),
                createPartFromString(event.getLocation()),
                createPartFromString(event.getStartDate()),
                createPartFromString(event.getFinishDate()),
                createPartFromString(event.getEventTimeStart()),
                createPartFromString(event.getEventTimeEnd())
        );
        call.enqueue(new Callback<Event>() {
            @Override
            public void onResponse(Call<Event> call, Response<Event> response) {
                if (response.isSuccessful()) {
                    Event newEvent = response.body();
                    if (newEvent != null) {
                        Log.d("AddEvent", "New Event ID: " + newEvent.getId());

                        Intent homeIntent = new Intent(AddEvent.this, HomeActivity.class);
                        startActivity(homeIntent);
                        finish();
                    }
                } else {
                    try {
                        String errorBody = response.errorBody().string();
                        Log.e("AddEvent", "Error response body: " + errorBody);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<Event> call, Throwable t) {
                Log.e("AddEvent", "API call failed: " + t.getMessage());
            }

        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri selectedImageUri = data.getData();
            String imagePath = getRealPathFromUri(AddEvent.this, selectedImageUri);

            // Update the ImageView in XML layout
            imageViewEventPoster.setImageURI(selectedImageUri);

            // Set the image path to the Event object
            event.setImagePath(imagePath);
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


    private void validateEventTitle(String eventTitle) {
        if (eventTitle.isEmpty()) {
            eventTitleEditText.setError("Event title cannot be empty");
        } else {
            eventTitleEditText.setError(null);
        }

        updateSaveButtonState();
    }

    private void validateEventDescription(String eventDescription) {
        if (eventDescription.isEmpty()) {
            eventDescEditText.setError("Event description cannot be empty");
        } else {
            eventDescEditText.setError(null);
        }

        updateSaveButtonState();
    }

    private void validateEventLocation(String eventLocation) {
        if (eventLocation.isEmpty()) {
            eventLocationEditText.setError("Event location cannot be empty");
        } else {
            eventLocationEditText.setError(null);
        }

        updateSaveButtonState();
    }

    private void validateEndDate(String endDate) {
        // Validate that the end date is not before the start date

        String startDate = startDateTextView.getText().toString().trim();
        Log.d("addevent", "is valid date: " + isValidDate(startDate, endDate));
        if (isValidDate(startDate, endDate)) {
            endDateTextView.setError(null);
        } else {
            endDateTextView.setError("End date cannot be before the start date");
        }
    }

    private boolean isValidDate(String startDate, String endDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm dd, MMM yyyy", Locale.getDefault());

        try {
            Date startDateObj = dateFormat.parse(startDate);
            Date endDateObj = dateFormat.parse(endDate);

            // Check if the end date is not before the start date
            if (endDateObj.before(startDateObj)) {
                return false;
            }
        } catch (ParseException e) {
            e.printStackTrace();
            return false; // Handle parsing errors
        }
        return true;
    }


    private void updateSaveButtonState() {
        String eventTitle = eventTitleEditText.getText().toString().trim();
        String eventDes = eventDescEditText.getText().toString().trim();
        String eventLocation = eventLocationEditText.getText().toString().trim();
        boolean isEndDateValid = isValidDate(startDateTextView.getText().toString().trim(), endDateTextView.getText().toString().trim());
        boolean isImageSelected = event.getImagePath() != null;

        boolean isTitleValid = !eventTitle.isEmpty();
        boolean isDescValid = !eventDes.isEmpty();
        boolean isLocationValid = !eventLocation.isEmpty();

        saveButton.setEnabled(isTitleValid && isDescValid &&
                isLocationValid && isEndDateValid && isImageSelected);
    }

}
