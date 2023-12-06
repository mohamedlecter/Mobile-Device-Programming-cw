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
    private static final int STORAGE_PERMISSION_CODE = 100;

    private static final String TAG = "PERMISSION_TAG";

    private ImageView imageViewEventPoster;
    private ImageView backButton;
    private Button btnPickImage;

    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        api = RetrofitClient.getInstance().getApi(); // gets the api from the retrofit client


        // Initialize UI elements
        eventTitleEditText = findViewById(R.id.eventTitle);
        eventDescEditText = findViewById(R.id.eventDsc);
        eventLocationEditText = findViewById(R.id.eventLocation);

        startDateTextView = findViewById(R.id.startDate);
        endDateTextView = findViewById(R.id.endDate);

        imageViewEventPoster = findViewById(R.id.imageViewEventPoster);
        btnPickImage = findViewById(R.id.btnPickImage);
        backButton = findViewById(R.id.buttonBackToEvents);


        sessionManager = new SessionManager(this);
        calendarStart = Calendar.getInstance();
        calendarEnd = Calendar.getInstance();
        event = new Event(); // Initialize the Event object

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an intent to go back to the home page
                Intent homeIntent = new Intent(AddEvent.this, HomeActivity.class);
                startActivity(homeIntent);
            }
        });

        btnPickImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickImage();
            }
        });

        // Set up the click listener for the date and time picker
        startDateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateTimePicker(true); // Pass true to indicate the start date/time
            }
        });

        endDateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateTimePicker(false); // Pass false to indicate the end date/time
            }
        });

        Button saveButton = findViewById(R.id.buttonSave);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Populate the Event object with data
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
        });
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
            imageViewEventPoster.setImageURI(selectedImageUri);

            // Set the image path to the Event object
            event.setImagePath(imagePath);
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
                            Toast.makeText(AddEvent.this, "Manage External Storage Permission is denied", Toast.LENGTH_SHORT).show();
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
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

                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
                                String selectedDateTime = sdf.format(calendar.getTime());

                                // Set the selected date and time to the appropriate TextView
                                if (isStartDate) {
                                    startDateTextView.setText(selectedDateTime);
                                    calendarStart = calendar;
                                    event.setEventTimeStart(selectedDateTime);
                                } else {
                                    endDateTextView.setText(selectedDateTime);
                                    calendarEnd = calendar;
                                    event.setEventTimeEnd(selectedDateTime);
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
