package com.example.mad;

public class Leave {
    private String id;
    private String studentId;
    private String studentName;
    private String room;
    private String hostel;
    private String leaveTakenOn;
    private String duration;
    private String expectedReturn;
    private String imageUrl;

    public Leave() {
        // Default constructor required for Firebase
    }

    public Leave(String id, String studentId, String studentName, String room, String hostel,
                 String leaveTakenOn, String duration, String expectedReturn, String imageUrl) {
        this.id = id;
        this.studentId = studentId;
        this.studentName = studentName;
        this.room = room;
        this.hostel = hostel;
        this.leaveTakenOn = leaveTakenOn;
        this.duration = duration;
        this.expectedReturn = expectedReturn;
        this.imageUrl = imageUrl;
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getRoom() { return room; }
    public void setRoom(String room) { this.room = room; }

    public String getHostel() { return hostel; }
    public void setHostel(String hostel) { this.hostel = hostel; }

    public String getLeaveTakenOn() { return leaveTakenOn; }
    public void setLeaveTakenOn(String leaveTakenOn) { this.leaveTakenOn = leaveTakenOn; }

    public String getDuration() { return duration; }
    public void setDuration(String duration) { this.duration = duration; }

    public String getExpectedReturn() { return expectedReturn; }
    public void setExpectedReturn(String expectedReturn) { this.expectedReturn = expectedReturn; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}