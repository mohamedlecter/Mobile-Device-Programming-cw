package com.example.cw.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cw.R;
import com.example.cw.model.Job;

import java.util.List;

public class JobAdapter extends RecyclerView.Adapter<JobAdapter.JobViewHolder> {
    private List<Job> jobs;
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public JobAdapter(List<Job> jobs) {
        this.jobs = jobs;
    }

    @NonNull
    @Override
    public JobViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_job, parent, false);
        return new JobViewHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull JobViewHolder holder, int position) {
        Job job = jobs.get(position);
        holder.bind(job);
    }

    @Override
    public int getItemCount() {
        return jobs.size();
    }

    public static class JobViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView jobTitleTextView;
        private final TextView jobDescTextView;
        private final TextView jobLocationTextView;


        private OnItemClickListener listener;

        public JobViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);
            itemView.setOnClickListener(this);
            this.listener = listener;
            jobTitleTextView = itemView.findViewById(R.id.jobTitle);
            jobDescTextView = itemView.findViewById(R.id.jobDescription);
            jobLocationTextView = itemView.findViewById(R.id.jobLocation);

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

        public void bind(Job job) {
            jobTitleTextView.setText(job.getTitle());
            jobDescTextView.setText(job.getDescription());
            jobLocationTextView.setText(job.getLocation());


        }
    }
}
