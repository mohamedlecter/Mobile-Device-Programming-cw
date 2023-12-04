package com.example.cw.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cw.R;
import com.example.cw.SessionManager;
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
        private final TextView eventDateTextView;
        private final TextView eventLocationTextView;
        private final ImageView eventImageView;
        private OnItemClickListener listener;

        public EventViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);
            itemView.setOnClickListener(this); // to get hold of the clicked event
            this.listener = listener;
            eventNameTextView = itemView.findViewById(R.id.eventName);
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
                // Format and display date, day, month, and time
                String formattedYear = String.valueOf(event.getYear());
                String formattedDay = String.valueOf(event.getDay());
                String formattedMonth = event.getMonth();

                String displayDateTime = String.format("%s, %s %s", formattedDay, formattedMonth, formattedYear);

                eventNameTextView.setText(event.getTitle());
                eventDateTextView.setText(displayDateTime);
                eventLocationTextView.setText(event.getLocation());

                try {
//                http://:4000/uploads/1700915869210ICN.jpg
                String serverBaseUrl = "http://10.0.2.2:4000";
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

                // Check if the user is an admin
                SessionManager sessionManager = new SessionManager(itemView.getContext());
                boolean isAdmin = sessionManager.isAdmin();

                // Set the visibility of the eventActions LinearLayout based on admin status
                LinearLayout eventActionsLayout = itemView.findViewById(R.id.eventActions);
                if (isAdmin) {
                    eventActionsLayout.setVisibility(View.VISIBLE);
                } else {
                    eventActionsLayout.setVisibility(View.GONE);
                }

            }
        }
    }
}
