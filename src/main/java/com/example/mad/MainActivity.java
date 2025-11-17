package com.example.mad;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

public class MainActivity extends AppCompatActivity {

    private EditText signupName, signupEmail, signupPassword, signupConfirmPassword;
    private EditText signupDepartment, signupUSN, signupEmployeeId;
    private TextInputLayout signupDepartmentLayout, signupUSNLayout, signupEmployeeIdLayout;
    private MaterialAutoCompleteTextView signupRole;
    private MaterialButton btnSignup;
    private TextView goToLogin;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        signupName = findViewById(R.id.signupName);
        signupEmail = findViewById(R.id.signupEmail);
        signupPassword = findViewById(R.id.signupPassword);
        signupConfirmPassword = findViewById(R.id.signupConfirmPassword);
        signupDepartment = findViewById(R.id.signupDepartment);
        signupUSN = findViewById(R.id.signupUSN);
        signupEmployeeId = findViewById(R.id.signupEmployeeId);
        signupDepartmentLayout = findViewById(R.id.signupDepartmentLayout);
        signupUSNLayout = findViewById(R.id.signupUSNLayout);
        signupEmployeeIdLayout = findViewById(R.id.signupEmployeeIdLayout);
        signupRole = findViewById(R.id.signupRole);
        btnSignup = findViewById(R.id.btnSignup);
        goToLogin = findViewById(R.id.goToLogin);

        // Populate role dropdown
        String[] roles = new String[]{"Student", "Warden", "Staff"};
        android.widget.ArrayAdapter<String> adapter = new android.widget.ArrayAdapter<>(
                this, android.R.layout.simple_dropdown_item_1line, roles);
        signupRole.setAdapter(adapter);

        signupRole.setOnItemClickListener((parent, view, position, id) -> {
            String role = parent.getItemAtPosition(position).toString();
            if ("Student".equalsIgnoreCase(role)) {
                signupDepartmentLayout.setVisibility(android.view.View.VISIBLE);
                signupUSNLayout.setVisibility(android.view.View.VISIBLE);
                signupEmployeeIdLayout.setVisibility(android.view.View.GONE);
            } else {
                // Warden / Staff: hide department and USN, show employee id
                signupDepartmentLayout.setVisibility(android.view.View.GONE);
                signupUSNLayout.setVisibility(android.view.View.GONE);
                signupEmployeeIdLayout.setVisibility(android.view.View.VISIBLE);
            }
        });

        // Default: assume Student until role chosen
        signupDepartmentLayout.setVisibility(android.view.View.VISIBLE);
        signupUSNLayout.setVisibility(android.view.View.VISIBLE);
        signupEmployeeIdLayout.setVisibility(android.view.View.GONE);

        btnSignup.setOnClickListener(v -> registerUser());

        goToLogin.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        });
    }

    private void registerUser() {
        String name = signupName.getText().toString().trim();
        String email = signupEmail.getText().toString().trim();
        String password = signupPassword.getText().toString().trim();
        String confirmPassword = signupConfirmPassword.getText().toString().trim();
        String department = signupDepartment.getText().toString().trim();
        String usn = signupUSN.getText().toString().trim();
        String employeeId = signupEmployeeId.getText() != null ? signupEmployeeId.getText().toString().trim() : "";
        String role = signupRole.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            signupName.setError("Enter full name");
            return;
        }
        if (TextUtils.isEmpty(email)) {
            signupEmail.setError("Enter email");
            return;
        }
        if ("Warden".equalsIgnoreCase(role) && !"bhalu1243@gmail.com".equalsIgnoreCase(email)) {
            signupEmail.setError("Only fixed warden email is allowed");
            return;
        }
        if ("Student".equalsIgnoreCase(role) && TextUtils.isEmpty(department)) {
            signupDepartment.setError("Enter department");
            return;
        }
        if (TextUtils.isEmpty(role)) {
            signupRole.setError("Select role");
            return;
        }
        if ("Student".equalsIgnoreCase(role)) {
            if (TextUtils.isEmpty(usn)) {
                signupUSN.setError("Enter USN");
                return;
            }
        } else {
            if (TextUtils.isEmpty(employeeId)) {
                signupEmployeeId.setError("Enter Employee ID");
                return;
            }
        }
        if (TextUtils.isEmpty(password)) {
            signupPassword.setError("Enter password");
            return;
        }
        if (password.length() < 6) {
            signupPassword.setError("Password must be at least 6 characters");
            return;
        }
        if (!password.equals(confirmPassword)) {
            signupConfirmPassword.setError("Passwords do not match");
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        String uid = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : null;
                        if (uid == null) {
                            Toast.makeText(MainActivity.this, "Signup error: UID not found", Toast.LENGTH_LONG).show();
                            return;
                        }

                        java.util.HashMap<String, Object> profile = new java.util.HashMap<>();
                        profile.put("uid", uid);
                        profile.put("name", name);
                        profile.put("email", email);
                        if (!TextUtils.isEmpty(department)) {
                            profile.put("department", department);
                        }
                        if ("Student".equalsIgnoreCase(role)) {
                            profile.put("usn", usn);
                        } else {
                            profile.put("employeeId", employeeId);
                        }
                        profile.put("role", role);
                        profile.put("createdAt", System.currentTimeMillis());

                        db.collection("users").document(uid)
                                .set(profile, SetOptions.merge())
                                .addOnSuccessListener(unused -> {
                                    Toast.makeText(MainActivity.this, "Signup successful!", Toast.LENGTH_SHORT).show();
                                    Intent intent;
                                    if ("Warden".equalsIgnoreCase(role)) {
                                        intent = new Intent(MainActivity.this, WardenReviewActivity.class);
                                    } else if ("Staff".equalsIgnoreCase(role)) {
                                        intent = new Intent(MainActivity.this, StaffApproveActivity.class);
                                    } else {
                                        intent = new Intent(MainActivity.this, StudentRaiseComplaintActivity.class);
                                    }
                                    startActivity(intent);
                                    finish();
                                })
                                .addOnFailureListener(e ->
                                        Toast.makeText(MainActivity.this, "Failed to save profile: " + e.getMessage(), Toast.LENGTH_LONG).show()
                                );
                    } else {
                        Toast.makeText(MainActivity.this,
                                "Signup failed: " + task.getException().getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }
}


