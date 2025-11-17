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
import java.util.HashMap;
import java.util.List;

public class StaffApproveActivity extends AppCompatActivity {
    private RecyclerView rv;
    private ComplaintAdapter adapter;
    private final List<Complaint> complaints = new ArrayList<>();
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_approve);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        rv = findViewById(R.id.rvStaffComplaints);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ComplaintAdapter(new ComplaintAdapter.OnItemClickListener() {
            @Override public void onItemClick(Complaint c) { updateStatus(c, "Ongoing"); }
            @Override public void onItemLongClick(Complaint c) { updateStatus(c, "Completed"); }
        });
        rv.setAdapter(adapter);
        loadAssignedComplaints();

        findViewById(R.id.btnLogoutStaff).setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new android.content.Intent(this, LoginActivity.class));
            finish();
        });
    }

    private void loadAssignedComplaints() {
        String uid = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : null;
        if (uid == null) { Toast.makeText(this, "Not logged in", Toast.LENGTH_SHORT).show(); return; }
        db.collection("complaints")
                .whereEqualTo("assignedStaffUid", uid)
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

    private void updateStatus(Complaint c, String status) {
        HashMap<String, Object> upd = new HashMap<>();
        upd.put("status", status);
        db.collection("complaints").document(c.id)
                .update(upd)
                .addOnSuccessListener(unused -> Toast.makeText(this, status, Toast.LENGTH_SHORT).show())
                .addOnFailureListener(err -> Toast.makeText(this, err.getMessage(), Toast.LENGTH_LONG).show());
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
