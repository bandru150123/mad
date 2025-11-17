package com.example.mad.models;

public class AttendanceRecord {
    private String id;
    private String name;
    private String profileUrl;
    private String time;        // e.g., "09:30 AM"
    private String roomHostel;  // e.g., "Room 101 • Hostel H1"
    private String status;      // "In", "Out", "Leave"

    public AttendanceRecord() { } // empty constructor for Firestore

    public AttendanceRecord(String id, String name, String profileUrl, String time, String roomHostel, String status) {
        this.id = id;
        this.name = name;
        this.profileUrl = profileUrl;
        this.time = time;
        this.roomHostel = roomHostel;
        this.status = status;
    }

    // getters & setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getProfileUrl() { return profileUrl; }
    public void setProfileUrl(String profileUrl) { this.profileUrl = profileUrl; }

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }

    public String getRoomHostel() { return roomHostel; }
    public void setRoomHostel(String roomHostel) { this.roomHostel = roomHostel; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
