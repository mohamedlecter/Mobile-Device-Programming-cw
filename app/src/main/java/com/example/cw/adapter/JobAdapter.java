package com.example.cw.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cw.R;
import com.example.cw.SessionManager;
import com.example.cw.model.Job;

import java.util.List;

public class JobAdapter extends RecyclerView.Adapter<JobAdapter.JobViewHolder> {
    private List<Job> jobs;
    private JobAdapter.OnItemClickListener itemClickListener;
    private JobAdapter.OnEditClickListener editClickListener;
    private JobAdapter.OnDeleteClickListener deleteClickListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public interface OnEditClickListener {
        void onEditClick(int position);
    }

    public interface OnDeleteClickListener {
        void onDeleteClick(int position);
    }

    public JobAdapter(List<Job> jobs) {
        this.jobs = jobs;
    }

    @NonNull
    @Override
    public JobViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_job, parent, false);
        return new JobViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull JobViewHolder holder, int position) {
        Job job = jobs.get(position);
        holder.bind(job);

        final int adapterPosition = holder.getAdapterPosition();
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemClickListener != null) {
                    itemClickListener.onItemClick(adapterPosition);
                }
            }
        });

        holder.editJobButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editClickListener != null) {
                    editClickListener.onEditClick(adapterPosition);
                }
            }
        });

        holder.deleteJobButton.setOnClickListener(new View.OnClickListener() {
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
        return jobs.size();
    }
    public void setOnItemClickListener(JobAdapter.OnItemClickListener listener) {
        this.itemClickListener = listener;
    }

    public void setOnEditClickListener(JobAdapter.OnEditClickListener listener) {
        this.editClickListener = listener;
    }

    public void setOnDeleteClickListener(JobAdapter.OnDeleteClickListener listener) {
        this.deleteClickListener = listener;
    }

    public static class JobViewHolder extends RecyclerView.ViewHolder {
        private final TextView jobTitleTextView;
        private final TextView jobLocationTextView;
        private final TextView jobDurationTextView;
        private final TextView jobSalaryTextView;

        private ImageButton editJobButton;

        private  ImageButton deleteJobButton;

        public JobViewHolder(@NonNull View itemView ) {
            super(itemView);
            jobTitleTextView = itemView.findViewById(R.id.jobTitle);
            jobLocationTextView = itemView.findViewById(R.id.jobLocation);
            jobDurationTextView = itemView.findViewById(R.id.jobDuration);
            jobSalaryTextView = itemView.findViewById(R.id.jobSalary);

            editJobButton = itemView.findViewById(R.id.editJob);
            deleteJobButton = itemView.findViewById(R.id.deleteJob);
        }

        public void bind(Job job) {
            String jobDurationStart = job.getJobDurationStart();
            String jobDurationEnd = job.getJobDurationEnd();

            String displayjobDuration = String.format("%s - %s", jobDurationStart, jobDurationEnd);

            String jobSalary = String.valueOf(job.getSalary());

            String displaySalary = String.format("%s$ per hour ", jobSalary);

            jobTitleTextView.setText(job.getTitle());
            jobLocationTextView.setText(job.getLocation());
            jobDurationTextView.setText(displayjobDuration);
            jobSalaryTextView.setText(displaySalary);


            // Check if the user is an admin
            SessionManager sessionManager = new SessionManager(itemView.getContext());
            boolean isAdmin = sessionManager.isAdmin();

            LinearLayout jobActionsLayout = itemView.findViewById(R.id.jobActions);
            if (isAdmin) {
                jobActionsLayout.setVisibility(View.VISIBLE);
            } else {
                jobActionsLayout.setVisibility(View.GONE);
            }
        }


    }
}
