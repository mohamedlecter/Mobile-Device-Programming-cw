package com.example.cw.adapter;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cw.R;
import com.example.cw.model.Link;

import java.util.List;

public class LinksAdapter extends RecyclerView.Adapter<LinksAdapter.LinksViewHolder> {
    private List<Link> links;
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public LinksAdapter(List<Link> links) {
        this.links = links;
    }

    @NonNull
    @Override
    public LinksViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_link, parent, false);
        return new LinksViewHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull LinksViewHolder holder, int position) {
        Link link = links.get(position);
        holder.bind(link);
    }

    @Override
    public int getItemCount() {
        return links.size();
    }

    public static class LinksViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView titleTextView;
        private final TextView descriptionTextView;

        private LinksAdapter.OnItemClickListener listener;

        public LinksViewHolder(@NonNull View itemView, LinksAdapter.OnItemClickListener listener) {
            super(itemView);
            itemView.setOnClickListener(this);
            this.listener = listener;
            titleTextView = itemView.findViewById(R.id.linkTitle);
            descriptionTextView = itemView.findViewById(R.id.lnkDescription);

        }

        @Override
        public void onClick(View view) {
            if (listener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(position);
                }
            }
        }

        public void bind(Link link) {
            titleTextView.setText(link.getTitle());
            descriptionTextView.setText(link.getDescription());
        }
    }
}
