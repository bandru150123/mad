package com.example.mad.models;

public class Complaint {
    public String id;
    public String title;
    public String description;
    public String category;
    public String room;
    public String status; // Pending, Ongoing, Completed
    public String studentUid;
    public String assignedStaffUid;
    public long createdAt;

    public Complaint() {}
}
