package com.example.cw;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cw.model.Event;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {
    private List<Event> events;
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public EventAdapter(List<Event> events) {
        this.events = events;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_event, parent, false);
        return new EventViewHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event event = events.get(position);
        holder.bind(event);
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView eventNameTextView;
        private final TextView eventDescTextView;
        private final TextView eventDateTextView;
        private final TextView eventLocationTextView;
        private final ImageView eventImageView;
        private OnItemClickListener listener;

        public EventViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);
            itemView.setOnClickListener(this); // to get hold of the clicked event
            this.listener = listener;
            eventNameTextView = itemView.findViewById(R.id.eventName);
            eventDescTextView = itemView.findViewById(R.id.eventDescription);
            eventDateTextView = itemView.findViewById(R.id.eventDateTime);
            eventLocationTextView = itemView.findViewById(R.id.eventLocation);
            eventImageView = itemView.findViewById(R.id.eventImage);
        }

        public void onClick(View view) {
            if (listener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(position);
                }
            }
        }

        public void bind(Event event) {

            if (event == null || event.getImagePath() == null || event.getImagePath().isEmpty()) {
                Log.e("EventAdapter", "Invalid image path");
            } else {
                eventNameTextView.setText(event.getTitle());
                eventDescTextView.setText(event.getDescription());
                eventDateTextView.setText(event.getDate());
                eventLocationTextView.setText(event.getLocation());

                // Log the image path to check if it's null
//            Log.d("ImagePath", event.getImagePath());
//
//            try {
////                http://:4000/uploads/1700915869210ICN.jpg
//                // Replace backslashes with forward slashes and add the file URI scheme
////                String serverBaseUrl = "http://10.0.2.2:4000";
//                String serverBaseUrl = "http://localhost:4000";
//                String imagePath = serverBaseUrl + "/" + event.getImagePath().replace("\\", "/");
//                // Log the image path to check if it's null
//                Log.d("ImagePath 2", imagePath);
//                // Load the image using Picasso
////                Picasso.get().load(imagePath).into(eventImageView);
//                Picasso.get()
//                        .load(imagePath)
//                        .into(eventImageView, new Callback() {
//                            @Override
//                            public void onSuccess() {
//                                Log.d("Picasso", "Image loaded successfully");
//                            }
//
//                            @Override
//                            public void onError(Exception e) {
//                                Log.e("Picasso", "Error loading image: " + e.getMessage());
//                            }
//                        });
//
//
//
//            } catch (IllegalArgumentException e) {
//                // Log an error or provide a placeholder image if needed
//                Log.e("EventAdapter", "Failed to load image: " + e.getMessage());
//            }
//        }
            }

        }
    }
}
