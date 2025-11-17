package com.example.mad;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.app.AlertDialog;
import android.widget.*;
import android.view.*;
import android.graphics.Color;
import android.content.Intent;
import java.util.*;

public class RoomActivity extends AppCompatActivity {

    private Map<String, List<Room>> buildingData = new HashMap<>();
    private LinearLayout rootLayout;
    private TextView titleText;
    private ScrollView scrollView;
    private GridLayout gridLayout;
    private ListView listView;
    private Button homeButton;

    private String currentFloor = null; // null = floor list mode

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Root container
        rootLayout = new LinearLayout(this);
        rootLayout.setOrientation(LinearLayout.VERTICAL);
        rootLayout.setBackgroundColor(Color.parseColor("#121212"));
        rootLayout.setPadding(40, 60, 40, 60);

        // Title
        titleText = new TextView(this);
        titleText.setText("🏢 Select a Floor");
        titleText.setTextSize(26);
        titleText.setTextColor(Color.parseColor("#00FFFF"));
        titleText.setGravity(Gravity.CENTER);
        titleText.setPadding(0, 0, 0, 40);
        rootLayout.addView(titleText);

        // “Home” button
        homeButton = new Button(this);
        homeButton.setText("🏠 Home");
        homeButton.setBackgroundColor(Color.parseColor("#333333"));
        homeButton.setTextColor(Color.WHITE);
        homeButton.setVisibility(View.GONE);
        homeButton.setOnClickListener(v -> {
            // Navigate to HomeActivity when clicked
            Intent intent = new Intent(RoomActivity.this, HomeActivity.class);
            startActivity(intent);
            finish(); // finish current activity so it doesn’t stack
        });
        rootLayout.addView(homeButton,
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        // Scroll area
        scrollView = new ScrollView(this);
        rootLayout.addView(scrollView, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1));

        // Floor list
        listView = new ListView(this);
        scrollView.addView(listView);

        setContentView(rootLayout);

        // Create floors and rooms
        generateDummyData();

        // Show floors at start
        showFloors();

        // Floor click
        listView.setOnItemClickListener((parent, view, position, id) -> {
            String floorName = (String) listView.getItemAtPosition(position);
            currentFloor = floorName;
            showRoomsForFloor(floorName);
        });
    }

    // Shows all floors
    private void showFloors() {
        currentFloor = null;
        titleText.setText("🏢 Select a Floor");
        homeButton.setVisibility(View.GONE);

        List<String> floors = new ArrayList<>(buildingData.keySet());
        Collections.sort(floors);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, floors);

        listView.setAdapter(adapter);
        scrollView.removeAllViews();
        scrollView.addView(listView);
    }

    // Shows rooms in grid for a given floor
    private void showRoomsForFloor(String floorName) {
        titleText.setText(floorName + " - Select a Room");
        homeButton.setVisibility(View.VISIBLE);
        scrollView.removeAllViews();

        gridLayout = new GridLayout(this);
        gridLayout.setColumnCount(3); // 3 columns for balanced layout
        gridLayout.setAlignmentMode(GridLayout.ALIGN_MARGINS);
        gridLayout.setUseDefaultMargins(true);
        gridLayout.setPadding(20, 20, 20, 20);

        List<Room> rooms = buildingData.get(floorName);

        for (Room room : rooms) {
            Button roomBtn = new Button(this);
            roomBtn.setText(room.roomName);
            roomBtn.setTextColor(Color.WHITE);
            roomBtn.setAllCaps(false);
            roomBtn.setPadding(20, 20, 20, 20);
            roomBtn.setBackgroundColor(room.isBooked ? Color.RED : Color.GREEN);

            // Add spacing between room buttons
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.setMargins(20, 20, 20, 20);
            params.width = 300;
            params.height = 180;
            roomBtn.setLayoutParams(params);

            roomBtn.setOnClickListener(v -> showRoomDialog(room));
            gridLayout.addView(roomBtn);
        }

        scrollView.addView(gridLayout);
    }

    // Dialog for booking/unbooking
    private void showRoomDialog(Room room) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(room.roomName);

        if (room.isBooked) {
            builder.setMessage("This room is currently booked.\nDo you want to mark it as available?");
            builder.setPositiveButton("Mark Available", (d, w) -> {
                room.isBooked = false;
                Toast.makeText(this, room.roomName + " is now available.", Toast.LENGTH_SHORT).show();
                showRoomsForFloor(currentFloor);
            });
        } else {
            builder.setMessage("This room is available.\nDo you want to book it?");
            builder.setPositiveButton("Book Room", (d, w) -> {
                room.isBooked = true;
                Toast.makeText(this, room.roomName + " booked successfully!", Toast.LENGTH_SHORT).show();
                showRoomsForFloor(currentFloor);
            });
        }

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    // Generate rooms for 4 floors
    private void generateDummyData() {
        for (int f = 1; f <= 4; f++) { // 4 floors
            List<Room> rooms = new ArrayList<>();
            for (int r = 1; r <= 25; r++) { // 25 rooms per floor
                boolean booked = Math.random() < 0.25; // 25% chance booked
                rooms.add(new Room("Room " + f + String.format("%02d", r), booked));
            }
            buildingData.put("Floor " + f, rooms);
        }
    }

    // Room data class
    static class Room {
        String roomName;
        boolean isBooked;

        Room(String name, boolean booked) {
            this.roomName = name;
            this.isBooked = booked;
        }
    }
}
