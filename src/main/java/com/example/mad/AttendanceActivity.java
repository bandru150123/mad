package com.example.mad;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mad.adapters.StudentsAdapter;
import com.example.mad.models.AttendanceRecord;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class AttendanceActivity extends AppCompatActivity {

    private RecyclerView recycler;
    private StudentsAdapter adapter;
    private List<AttendanceRecord> list = new ArrayList<>();
    private FirebaseFirestore firestore;
    private Button btnExport;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);

        // ✅ DO NOT reinitialize Firebase here
        // FirebaseApp.initializeApp(this);  <-- REMOVE this line

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        // 🔒 Check user session
        if (currentUser == null) {
            Log.e("AuthDebug", "No current user — redirecting to LoginActivity");
            Toast.makeText(this, "Session expired. Please log in again.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        } else {
            Log.d("AuthDebug", "User logged in: " + currentUser.getEmail());
        }

        firestore = FirebaseFirestore.getInstance();
        recycler = findViewById(R.id.recyclerStudents);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        adapter = new StudentsAdapter(this, list);
        recycler.setAdapter(adapter);

        btnExport = findViewById(R.id.btnExportData);
        btnExport.setOnClickListener(v ->
                Toast.makeText(this, "Export clicked (implement CSV/Excel)", Toast.LENGTH_SHORT).show()
        );

        loadAttendance();
    }

    private void loadAttendance() {
        firestore.collection("students")
                .get()
                .addOnSuccessListener((QuerySnapshot snaps) -> {
                    list.clear();
                    for (DocumentSnapshot doc : snaps.getDocuments()) {
                        String id = doc.getId();
                        String name = doc.getString("name");
                        String profile = doc.getString("profileImageUrl");
                        String room = doc.contains("room") ? doc.getString("room") : "Room -";
                        String hostel = doc.contains("hostel") ? doc.getString("hostel") : "H1";
                        String time = doc.contains("lastAttendanceTime") ? doc.getString("lastAttendanceTime") : "—";
                        String status = doc.contains("status") ? doc.getString("status") : "In";

                        list.add(new AttendanceRecord(
                                id, name, profile,
                                time, room + "  •  Hostel " + hostel, status
                        ));
                    }

                    adapter.notifyDataSetChanged();

                    if (list.isEmpty()) {
                        loadSample();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("FirestoreError", "Failed to load attendance data", e);
                    Toast.makeText(this, "Failed to load user info: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    loadSample();
                });
    }

    private void loadSample() {
        list.clear();
        for (int i = 1; i <= 8; i++) {
            list.add(new AttendanceRecord(
                    String.valueOf(i),
                    "Ramankant Sharma " + i,
                    null,
                    "09:3" + i + " AM",
                    "Room " + (100 + i) + "  •  Hostel H1",
                    "In"
            ));
        }
        adapter.notifyDataSetChanged();
    }
}
