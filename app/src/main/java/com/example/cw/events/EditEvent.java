package com.example.cw.events;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.cw.R;
import com.example.cw.SessionManager;
import com.example.cw.api.Api;
import com.example.cw.api.RetrofitClient;
import com.example.cw.model.Event;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

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
    private static final int STORAGE_PERMISSION_CODE = 100;

    private static final String TAG = "PERMISSION_TAG";


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
                showDateTimePicker(true); // Pass true to indicate the start date/time
            }
        });

        endDateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateTimePicker(false); // Pass false to indicate the end date/time
            }
        });

        pickImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickImage();
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

    private void showDateTimePicker(final boolean isStartDate) {
        final Calendar calendar = Calendar.getInstance();

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (DatePicker datePicker, int year, int month, int day) -> {
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, month);
                    calendar.set(Calendar.DAY_OF_MONTH, day);

                    // Show TimePicker after selecting the date
                    TimePickerDialog timePickerDialog = new TimePickerDialog(
                            this,
                            (TimePicker timePicker, int hour, int minute) -> {
                                calendar.set(Calendar.HOUR_OF_DAY, hour);
                                calendar.set(Calendar.MINUTE, minute);

                                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
                                String selectedTime = sdf.format(calendar.getTime());

                                SimpleDateFormat date = new SimpleDateFormat("dd, MMM yyyy", Locale.getDefault());
                                String selectedDate = date.format(calendar.getTime());


                                // Set the selected date and time to the appropriate TextView
                                if (isStartDate) {
                                    startDateEditText.setText(selectedTime + " " + selectedDate);
                                    calendarStart = calendar;
                                    selectedEvent.setEventTimeStart(selectedTime);
                                    selectedEvent.setStartDate(selectedDate);
                                } else {
                                    endDateEditText.setText(selectedTime + " " + selectedDate);
                                    calendarEnd = calendar;
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

    private void pickImage() {

        if (checkPermission()) {
            Log.d(TAG, "onClick: Permissions already granted...");
            startImagePicker();

        } else {
            Log.d(TAG, "onClick: Permissions was not granted, request...");
            requestPermission();
        }
    }


    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            //Android is 11(R) or above
            try {
                Log.d(TAG, "requestPermission: try");

                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                Uri uri = Uri.fromParts("package", this.getPackageName(), null);
                intent.setData(uri);
                storageActivityResultLauncher.launch(intent);
            } catch (Exception e) {
                Log.e(TAG, "requestPermission: catch", e);
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                storageActivityResultLauncher.launch(intent);
            }
        } else {
            //Android is below 11(R)
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                    STORAGE_PERMISSION_CODE
            );
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri selectedImageUri = data.getData();
            String imagePath = getRealPathFromUri(selectedImageUri);

            // Update the ImageView in XML layout
            editImageViewEvent.setImageURI(selectedImageUri);

            // Set the image path to the Event object
            selectedEvent.setImagePath(imagePath);
        }
    }


    private ActivityResultLauncher<Intent> storageActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    Log.d(TAG, "onActivityResult: ");
                    //here we will handle the result of our intent
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        //Android is 11(R) or above
                        if (Environment.isExternalStorageManager()) {
                            //Manage External Storage Permission is granted
                            Log.d(TAG, "onActivityResult: Manage External Storage Permission is granted");
                            startImagePicker();
                        } else {
                            //Manage External Storage Permission is denied
                            Log.d(TAG, "onActivityResult: Manage External Storage Permission is denied");
                            Toast.makeText(EditEvent.this, "Manage External Storage Permission is denied", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        //Android is below 11(R)
                    }
                }
            }
    );


    public boolean checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            //Android is 11(R) or above
            return Environment.isExternalStorageManager();
        } else {
            //Android is below 11(R)
            int write = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            int read = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);

            return write == PackageManager.PERMISSION_GRANTED && read == PackageManager.PERMISSION_GRANTED;
        }
    }

    /*Handle permission request results*/
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0) {
                //check each permission if granted or not
                boolean write = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                boolean read = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                if (write && read) {
                    //External Storage permissions granted
                    Log.d(TAG, "onRequestPermissionsResult: External Storage permissions granted");
                    startImagePicker();
                } else {
                    //External Storage permission denied
                    Log.d(TAG, "onRequestPermissionsResult: External Storage permission denied");
                    Toast.makeText(this, "External Storage permission denied", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void startImagePicker() {
        Intent pickIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/*");

        Intent chooserIntent = Intent.createChooser(pickIntent, "Select Image");
        try {
            startActivityForResult(chooserIntent, PICK_IMAGE_REQUEST);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    // Helper method to get the real path from the content URI
    private String getRealPathFromUri(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor == null) return null;

        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String path = cursor.getString(column_index);
        cursor.close();

        return path;
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