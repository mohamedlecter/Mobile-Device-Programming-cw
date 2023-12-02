package com.example.cw.model;

import java.io.Serializable;

public class Job implements Serializable {
    private String title;
    private String description;
    private String company;
    private String location;
    private int salary;
    private String createdAt;
    private String jobOrganizer;
    private String jobDurationStart;
    private String jobDurationEnd;
    private String jobHourStart;
    private String jobHourEnd;


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getSalary() {
        return salary;
    }

    public void setSalary(int salary) {
        this.salary = salary;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getJobOrganizer() {
        return jobOrganizer;
    }

    public void setJobOrganizer(String jobOrganizer) {
        this.jobOrganizer = jobOrganizer;
    }

    public String getJobDurationStart() {
        return jobDurationStart;
    }

    public void setJobDurationStart(String jobDurationStart) {
        this.jobDurationStart = jobDurationStart;
    }

    public String getJobDurationEnd() {
        return jobDurationEnd;
    }

    public void setJobDurationEnd(String jobDurationEnd) {
        this.jobDurationEnd = jobDurationEnd;
    }

    public String getJobHourStart() {
        return jobHourStart;
    }

    public void setJobHourStart(String jobHourStart) {
        this.jobHourStart = jobHourStart;
    }

    public String getJobHourEnd() {
        return jobHourEnd;
    }

    public void setJobHourEnd(String jobHourEnd) {
        this.jobHourEnd = jobHourEnd;
    }

}