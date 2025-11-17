package com.example.mad;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        TextView tvName = findViewById(R.id.tvProfileName);
        TextView tvEmail = findViewById(R.id.tvProfileEmail);
        TextView tvRole = findViewById(R.id.tvProfileRole);

        String uid = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : null;
        if (uid == null) {
            tvName.setText("Guest");
            tvEmail.setText("-");
            tvRole.setText("-");
            return;
        }

        db.collection("users").document(uid).get()
                .addOnSuccessListener((DocumentSnapshot d) -> {
                    tvName.setText(d.getString("name"));
                    tvEmail.setText(d.getString("email"));
                    String role = d.getString("role");
                    tvRole.setText(role != null ? role : "");
                });
    }
}
