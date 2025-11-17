package com.example.mad;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class document_upload extends AppCompatActivity {

    private static final String TAG = "DocumentUpload";
    private static final int PICK_AADHAAR = 201;
    private static final int PICK_ADMISSION = 202;
    private static final int PICK_PASSPORT = 203;

    private Button btnUploadAadhaar, btnUploadAdmission, btnUploadPassport, btnSave;
    private Uri aadhaarUri, admissionUri, passportUri;

    private StorageReference storageRef;
    private ProgressDialog progressDialog;

    // track pending uploads and downloaded URLs
    private int pendingUploads = 0;
    private final Map<String, String> downloadUrls = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.document_upload); // ensure layout name matches

        btnUploadAadhaar = findViewById(R.id.btnUploadAadhaar);
        btnUploadAdmission = findViewById(R.id.btnUploadAdmission);
        btnUploadPassport = findViewById(R.id.btnUploadPassport);
        btnSave = findViewById(R.id.btnSave);

        // Correct Firebase Storage reference (no local variable shadowing)
        storageRef = FirebaseStorage.getInstance().getReference().child("documents");

        btnUploadAadhaar.setOnClickListener(v -> openFileExplorer(PICK_AADHAAR));
        btnUploadAdmission.setOnClickListener(v -> openFileExplorer(PICK_ADMISSION));
        btnUploadPassport.setOnClickListener(v -> openFileExplorer(PICK_PASSPORT));

        btnSave.setOnClickListener(v -> uploadAllDocuments());
    }

    private void openFileExplorer(int requestCode) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*"); // use "application/pdf" or "image/*" if you want to restrict
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(Intent.createChooser(intent, "Select File"), requestCode);
    }

    @Override
    @SuppressWarnings("deprecation") // using startActivityForResult / onActivityResult
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri fileUri = data.getData();
            if (requestCode == PICK_AADHAAR) {
                aadhaarUri = fileUri;
                Toast.makeText(this, "Aadhaar selected", Toast.LENGTH_SHORT).show();
            } else if (requestCode == PICK_ADMISSION) {
                admissionUri = fileUri;
                Toast.makeText(this, "Admission selected", Toast.LENGTH_SHORT).show();
            } else if (requestCode == PICK_PASSPORT) {
                passportUri = fileUri;
                Toast.makeText(this, "Passport selected", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void uploadAllDocuments() {
        if (aadhaarUri == null && admissionUri == null && passportUri == null) {
            Toast.makeText(this, "Select at least one document", Toast.LENGTH_SHORT).show();
            return;
        }

        // Count how many uploads we will perform
        pendingUploads = 0;
        if (aadhaarUri != null) pendingUploads++;
        if (admissionUri != null) pendingUploads++;
        if (passportUri != null) pendingUploads++;

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading...");
        progressDialog.setMessage("Uploading documents, please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        if (aadhaarUri != null) {
            uploadSingle(aadhaarUri, "aadhaar_" + System.currentTimeMillis());
        }
        if (admissionUri != null) {
            uploadSingle(admissionUri, "admission_" + System.currentTimeMillis());
        }
        if (passportUri != null) {
            uploadSingle(passportUri, "passport_" + System.currentTimeMillis());
        }
    }

    private void uploadSingle(final Uri fileUri, final String nameKey) {
        // append random id to avoid collisions
        String filename = nameKey + "_" + UUID.randomUUID().toString();
        StorageReference fileRef = storageRef.child(filename);

        fileRef.putFile(fileUri)
                .addOnSuccessListener((OnSuccessListener<UploadTask.TaskSnapshot>) taskSnapshot -> {
                    // get download URL
                    fileRef.getDownloadUrl()
                            .addOnSuccessListener(uri -> {
                                String url = uri.toString();
                                downloadUrls.put(nameKey, url);
                                Log.d(TAG, "Uploaded " + nameKey + " -> " + url);
                                checkAllUploadsFinished();
                            })
                            .addOnFailureListener(e -> {
                                Log.e(TAG, "Failed to get download URL for " + nameKey, e);
                                Toast.makeText(document_upload.this, "Failed to get URL: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                checkAllUploadsFinished();
                            });
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Upload failed for " + nameKey, e);
                    Toast.makeText(document_upload.this, "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    checkAllUploadsFinished();
                });
    }

    private void checkAllUploadsFinished() {
        pendingUploads--;
        if (pendingUploads <= 0) {
            // All uploads finished
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            Toast.makeText(this, "All uploads finished", Toast.LENGTH_SHORT).show();
            // You can now save the downloadUrls map to your database if needed.
            // Example (pseudo):
            // saveUrlsToFirestore(downloadUrls);
            Log.d(TAG, "All uploaded URLs: " + downloadUrls.toString());
        } else {
            // Optionally update progress dialog message
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.setMessage("Remaining uploads: " + pendingUploads);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    // OPTIONAL: Implement saving URLs to Firestore/Realtime Database here if you want.
}
