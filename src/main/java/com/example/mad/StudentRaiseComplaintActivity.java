package com.example.mad;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class StudentRaiseComplaintActivity extends AppCompatActivity {

    private TextInputEditText complaintTitle, complaintDescription, complaintCategory, complaintRoom;
    private MaterialButton btnSubmitComplaint, btnMyComplaints, btnLogoutStudent;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_raise_complaint);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        complaintTitle = findViewById(R.id.complaintTitle);
        complaintDescription = findViewById(R.id.complaintDescription);
        complaintCategory = findViewById(R.id.complaintCategory);
        complaintRoom = findViewById(R.id.complaintRoom);
        btnSubmitComplaint = findViewById(R.id.btnSubmitComplaint);
        btnMyComplaints = findViewById(R.id.btnMyComplaints);
        btnLogoutStudent = findViewById(R.id.btnLogoutStudent);

        btnSubmitComplaint.setOnClickListener(v -> submitComplaint());
        btnMyComplaints.setOnClickListener(v -> {
            startActivity(new android.content.Intent(this, StudentComplaintsActivity.class));
        });
        btnLogoutStudent.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new android.content.Intent(this, LoginActivity.class));
            finish();
        });
    }

    private void submitComplaint() {
        String title = complaintTitle.getText() != null ? complaintTitle.getText().toString().trim() : "";
        String description = complaintDescription.getText() != null ? complaintDescription.getText().toString().trim() : "";
        String category = complaintCategory.getText() != null ? complaintCategory.getText().toString().trim() : "";
        String room = complaintRoom.getText() != null ? complaintRoom.getText().toString().trim() : "";

        if (TextUtils.isEmpty(title)) { complaintTitle.setError("Enter title"); return; }
        if (TextUtils.isEmpty(description)) { complaintDescription.setError("Enter description"); return; }
        if (TextUtils.isEmpty(category)) { complaintCategory.setError("Enter category"); return; }
        if (TextUtils.isEmpty(room)) { complaintRoom.setError("Enter room number"); return; }

        String uid = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : null;
        if (uid == null) { Toast.makeText(this, "Not logged in", Toast.LENGTH_SHORT).show(); return; }

        HashMap<String, Object> data = new HashMap<>();
        data.put("title", title);
        data.put("description", description);
        data.put("category", category);
        data.put("room", room);
        data.put("status", "Pending");
        data.put("studentUid", uid);
        data.put("createdAt", System.currentTimeMillis());

        db.collection("complaints")
                .add(data)
                .addOnSuccessListener(ref -> {
                    Toast.makeText(this, "Complaint submitted", Toast.LENGTH_SHORT).show();
                    complaintTitle.setText("");
                    complaintDescription.setText("");
                    complaintCategory.setText("");
                    // Navigate to user's complaints list
                    android.content.Intent intent = new android.content.Intent(StudentRaiseComplaintActivity.this, StudentComplaintsActivity.class);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed: " + e.getMessage(), Toast.LENGTH_LONG).show());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_role_switch, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_profile) {
            startActivity(new android.content.Intent(this, ProfileActivity.class));
            return true;
        } else if (id == R.id.action_logout) {
            FirebaseAuth.getInstance().signOut();
            startActivity(new android.content.Intent(this, LoginActivity.class));
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
