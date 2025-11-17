package com.example.mad;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class HomeActivity extends AppCompatActivity {

    private TextView welcomeText;
    private MaterialButton btnAttendance, btnDocuments, btnFees, btnVisitorLog, btnLogout, btnrooms;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mAuth = FirebaseAuth.getInstance();

        // ✅ Redirect if not logged in
        if (mAuth.getCurrentUser() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        db = FirebaseFirestore.getInstance();

        welcomeText = findViewById(R.id.welcomeText);
        btnAttendance = findViewById(R.id.btnAttendance);
        btnDocuments = findViewById(R.id.btnDocuments);
        btnFees = findViewById(R.id.btnFees);
        btnVisitorLog = findViewById(R.id.btnVisitorLog);
        btnLogout = findViewById(R.id.btnLogout);
        btnrooms=findViewById(R.id.btnrooms);

        String uid = mAuth.getUid();
        db.collection("users").document(uid).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String name = documentSnapshot.getString("name");
                        welcomeText.setText("Welcome, " + name);
                    } else {
                        welcomeText.setText("Welcome!");
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to load user info", Toast.LENGTH_SHORT).show()
                );

        btnAttendance.setOnClickListener(v ->
                startActivity(new Intent(this, AttendanceActivity.class))
        );

        btnDocuments.setOnClickListener(v ->
                startActivity(new Intent(this, document_upload.class))
        );

        btnFees.setOnClickListener(v ->
                startActivity(new Intent(this, FeeActivity.class))
        );

        btnrooms.setOnClickListener(v ->
                startActivity(new Intent(this, RoomActivity.class))
        );

        btnVisitorLog.setOnClickListener(v ->
                startActivity(new Intent(this, VisitorLogActivity.class))
        );

        btnLogout.setOnClickListener(v -> {
            mAuth.signOut();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }
}
