package com.example.mad;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import com.example.mad.adapters.ComplaintAdapter;
import com.example.mad.models.Complaint;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class StudentComplaintsActivity extends AppCompatActivity {
    private RecyclerView rv;
    private ComplaintAdapter adapter;
    private final List<Complaint> complaints = new ArrayList<>();
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_complaints);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        rv = findViewById(R.id.rvStudentComplaints);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ComplaintAdapter(new ComplaintAdapter.OnItemClickListener() {
            @Override public void onItemClick(Complaint c) { /* optional details in future */ }
            @Override public void onItemLongClick(Complaint c) { /* no-op */ }
        });
        rv.setAdapter(adapter);

        loadMyComplaints();

        findViewById(R.id.btnNewComplaint).setOnClickListener(v -> {
            startActivity(new android.content.Intent(this, StudentRaiseComplaintActivity.class));
        });

        findViewById(R.id.btnLogoutStudentComplaints).setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new android.content.Intent(this, LoginActivity.class));
            finish();
        });
    }

    private void loadMyComplaints() {
        String uid = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : null;
        if (uid == null) { Toast.makeText(this, "Not logged in", Toast.LENGTH_SHORT).show(); return; }
        db.collection("complaints")
                .whereEqualTo("studentUid", uid)
                .orderBy("createdAt")
                .addSnapshotListener((snap, e) -> {
                    if (e != null) { Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show(); return; }
                    complaints.clear();
                    if (snap != null) {
                        for (QueryDocumentSnapshot d : snap) {
                            Complaint c = d.toObject(Complaint.class);
                            c.id = d.getId();
                            complaints.add(c);
                        }
                    }
                    adapter.setItems(complaints);
                });
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
