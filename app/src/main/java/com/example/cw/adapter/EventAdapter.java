    package com.example.cw.adapter;
    
    import android.util.Log;
    import android.view.LayoutInflater;
    import android.view.View;
    import android.view.ViewGroup;
    import android.widget.ImageButton;
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
        private OnItemClickListener itemClickListener;
        private OnEditClickListener editClickListener;
        private OnDeleteClickListener deleteClickListener;
    
        public interface OnItemClickListener {
            void onItemClick(int position);
        }
    
        public interface OnEditClickListener {
            void onEditClick(int position);
        }
    
        public interface OnDeleteClickListener {
            void onDeleteClick(int position);
        }
    
        public EventAdapter(List<Event> events) {
            this.events = events;
        }
    
        @NonNull
        @Override
        public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_event, parent, false);
            return new EventViewHolder(view);
        }
    
        @Override
        public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
            Event event = events.get(position);
            holder.bind(event);
    
            final int adapterPosition = holder.getAdapterPosition();
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (itemClickListener != null) {
                        itemClickListener.onItemClick(adapterPosition);
                    }
                }
            });
            holder.editEventButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (editClickListener != null) {
                        editClickListener.onEditClick(adapterPosition);
                    }
                }
            });
    
            holder.deleteEventButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (deleteClickListener != null) {
                        deleteClickListener.onDeleteClick(adapterPosition);
                    }
                }
            });
        }
    
        @Override
        public int getItemCount() {
            return events.size();
        }
    
        public void setOnItemClickListener(OnItemClickListener listener) {
            this.itemClickListener = listener;
        }
    
        public void setOnEditClickListener(OnEditClickListener listener) {
            this.editClickListener = listener;
        }
    
        public void setOnDeleteClickListener(OnDeleteClickListener listener) {
            this.deleteClickListener = listener;
        }
    
        public static class EventViewHolder extends RecyclerView.ViewHolder {
            private final TextView eventNameTextView;
            private final TextView eventDateTextView;
            private final TextView eventLocationTextView;
            private final ImageView eventImageView;
            private ImageButton editEventButton;
    
            private  ImageButton deleteEventButton;
    
            public EventViewHolder(@NonNull View itemView) {
                super(itemView);
                eventNameTextView = itemView.findViewById(R.id.eventName);
                eventDateTextView = itemView.findViewById(R.id.eventDateTime);
                eventLocationTextView = itemView.findViewById(R.id.eventLocation);
                eventImageView = itemView.findViewById(R.id.eventImage);
                editEventButton = itemView.findViewById(R.id.editEvent);
                deleteEventButton = itemView.findViewById(R.id.deleteEvent);
            }
    
            public void bind(Event event) {
                if (event == null || event.getImagePath() == null || event.getImagePath().isEmpty()) {
                    Log.e("EventAdapter", "Invalid image path");
                } else {
                    String eventStartDate = event.getStartDate();
                    String eventStartEnd = event.getFinishDate();
    
                    String displayDateTime = String.format("%s - %s ", eventStartDate, eventStartEnd);
    
                    eventNameTextView.setText(event.getTitle());
                    eventDateTextView.setText(displayDateTime);
                    eventLocationTextView.setText(event.getLocation());
    
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
