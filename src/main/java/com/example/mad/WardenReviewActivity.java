package com.example.mad;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import com.example.mad.adapters.ComplaintAdapter;
import com.example.mad.models.Complaint;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class WardenReviewActivity extends AppCompatActivity {
    private RecyclerView rvComplaints;
    private ComplaintAdapter adapter;
    private final List<Complaint> complaints = new ArrayList<>();
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_warden_review);

        db = FirebaseFirestore.getInstance();
        rvComplaints = findViewById(R.id.rvComplaints);
        rvComplaints.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ComplaintAdapter(new ComplaintAdapter.OnItemClickListener() {
            @Override public void onItemClick(Complaint c) { showAssignDialog(c); }
            @Override public void onItemLongClick(Complaint c) { /* no-op */ }
        });
        rvComplaints.setAdapter(adapter);

        loadComplaints();

        findViewById(R.id.btnLogoutWarden).setOnClickListener(v -> {
            com.google.firebase.auth.FirebaseAuth.getInstance().signOut();
            startActivity(new android.content.Intent(this, LoginActivity.class));
            finish();
        });
    }

    private void loadComplaints() {
        db.collection("complaints")
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

    private void showAssignDialog(Complaint complaint) {
        final MaterialAutoCompleteTextView input = new MaterialAutoCompleteTextView(this);
        input.setHint("Select Staff");

        db.collection("users").whereEqualTo("role", "Staff").get()
                .addOnSuccessListener(q -> {
                    List<String> names = new ArrayList<>();
                    List<String> uids = new ArrayList<>();
                    for (QueryDocumentSnapshot d : q) {
                        names.add(d.getString("name"));
                        uids.add(d.getId());
                    }
                    ArrayAdapter<String> a = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, names);
                    input.setAdapter(a);

                    new MaterialAlertDialogBuilder(this)
                            .setTitle("Assign to Staff")
                            .setView(input)
                            .setPositiveButton("Assign", (dialog, which) -> {
                                int idx = names.indexOf(input.getText().toString());
                                if (idx >= 0) {
                                    String staffUid = uids.get(idx);
                                    HashMap<String, Object> upd = new HashMap<>();
                                    upd.put("assignedStaffUid", staffUid);
                                    upd.put("status", "Ongoing");
                                    db.collection("complaints").document(complaint.id)
                                            .update(upd)
                                            .addOnSuccessListener(unused -> Toast.makeText(this, "Assigned", Toast.LENGTH_SHORT).show())
                                            .addOnFailureListener(er -> Toast.makeText(this, er.getMessage(), Toast.LENGTH_LONG).show());
                                } else {
                                    Toast.makeText(this, "Select a valid staff", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .setNegativeButton("Cancel", null)
                            .show();
                })
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
