package com.example.mad;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    private EditText loginEmail, loginPassword;
    private MaterialButton btnLogin;
    private TextView goToSignup;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        loginEmail = findViewById(R.id.loginEmail);
        loginPassword = findViewById(R.id.loginPassword);
        btnLogin = findViewById(R.id.btnLogin);
        goToSignup = findViewById(R.id.goToSignup);

        btnLogin.setOnClickListener(v -> loginUser());

        goToSignup.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
        });
    }

    private void loginUser() {
        String email = loginEmail.getText().toString().trim();
        String password = loginPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            loginEmail.setError("Enter email");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            loginPassword.setError("Enter password");
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        String uid = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : null;
                        if (uid == null) {
                            Toast.makeText(LoginActivity.this, "Login error: UID not found", Toast.LENGTH_LONG).show();
                            return;
                        }
                        db.collection("users").document(uid).get()
                                .addOnSuccessListener(snapshot -> {
                                    String role = snapshot != null ? snapshot.getString("role") : null;
                                    String emailCurrent = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getEmail() : null;
                                    if (role != null && role.equalsIgnoreCase("Warden")) {
                                        // Only allow fixed demo warden account
                                        if (emailCurrent == null || !emailCurrent.equalsIgnoreCase("bhalu1243@gmail.com")) {
                                            Toast.makeText(LoginActivity.this, "Only demo warden account is allowed", Toast.LENGTH_LONG).show();
                                            mAuth.signOut();
                                            return;
                                        }
                                    }
                                    Intent intent;
                                    if (role == null || role.equalsIgnoreCase("Student")) {
                                        intent = new Intent(LoginActivity.this, StudentRaiseComplaintActivity.class);
                                    } else if (role.equalsIgnoreCase("Warden")) {
                                        intent = new Intent(LoginActivity.this, WardenReviewActivity.class);
                                    } else if (role.equalsIgnoreCase("Staff")) {
                                        intent = new Intent(LoginActivity.this, StaffApproveActivity.class);
                                    } else {
                                        intent = new Intent(LoginActivity.this, StudentRaiseComplaintActivity.class);
                                    }
                                    Toast.makeText(LoginActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();
                                    startActivity(intent);
                                    finish();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(LoginActivity.this, "Failed to load profile: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                });
                    } else {
                        Toast.makeText(LoginActivity.this,
                                "Login failed: " + task.getException().getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }
}

