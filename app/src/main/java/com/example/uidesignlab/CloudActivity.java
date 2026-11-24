package com.example.uidesignlab;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class CloudActivity extends AppCompatActivity {

    private static final String TAG = "CloudLab";

    private EditText editUserId, editName, editRole;
    private TextView txtCloudData;

    private DatabaseReference databaseReference;
    private ValueEventListener usersListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cloud);

        editUserId = findViewById(R.id.editUserId);
        editName = findViewById(R.id.editName);
        editRole = findViewById(R.id.editRole);
        txtCloudData = findViewById(R.id.txtCloudData);

        Button btnSaveUser = findViewById(R.id.btnSaveUser);
        Button btnUpdateRole = findViewById(R.id.btnUpdateRole);
        Button btnDeleteUser = findViewById(R.id.btnDeleteUser);

        databaseReference = FirebaseDatabase.getInstance().getReference();

        btnSaveUser.setOnClickListener(v -> saveUser());

        btnUpdateRole.setOnClickListener(v -> updateUser());

        btnDeleteUser.setOnClickListener(v -> deleteUser());

        usersListener = new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                StringBuilder result = new StringBuilder();

                if (!snapshot.exists()) {
                    txtCloudData.setText("No users in the cloud yet");
                    return;
                }

                for (DataSnapshot child : snapshot.getChildren()) {

                    User user = child.getValue(User.class);

                    if (user != null) {

                        result.append(child.getKey())
                                .append(": ")
                                .append(user.getName())
                                .append(" (")
                                .append(user.getRole())
                                .append(")\n\n");
                    }
                }

                txtCloudData.setText(result.toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Log.w(TAG, "Database read failed: " + error.getMessage());
            }
        };
    }

    private void saveUser() {

        String userId = editUserId.getText().toString().trim();
        String name = editName.getText().toString().trim();
        String role = editRole.getText().toString().trim();

        if (userId.isEmpty() || name.isEmpty() || role.isEmpty()) {

            Toast.makeText(this, "Please fill all fields",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        User newUser = new User(name, role);

        databaseReference.child("users")
                .child(userId)
                .setValue(newUser)
                .addOnSuccessListener(unused -> {

                    Toast.makeText(this, "Saved to the cloud",
                            Toast.LENGTH_SHORT).show();

                    editUserId.setText("");
                    editName.setText("");
                    editRole.setText("");
                })
                .addOnFailureListener(e -> {

                    Toast.makeText(this, "Save failed: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                });
    }

    private void updateUser() {

        String userId = editUserId.getText().toString().trim();
        String role = editRole.getText().toString().trim();

        if (userId.isEmpty() || role.isEmpty()) {

            Toast.makeText(this, "Enter User ID and New Role",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> updateMap = new HashMap<>();
        updateMap.put("role", role);

        databaseReference.child("users")
                .child(userId)
                .updateChildren(updateMap)
                .addOnSuccessListener(unused -> {

                    Toast.makeText(this, "User role updated",
                            Toast.LENGTH_SHORT).show();

                    editUserId.setText("");
                    editRole.setText("");
                })
                .addOnFailureListener(e -> {

                    Toast.makeText(this, "Update failed: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                });
    }

    private void deleteUser() {

        String userId = editUserId.getText().toString().trim();

        if (userId.isEmpty()) {

            Toast.makeText(this, "Enter User ID",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        databaseReference.child("users")
                .child(userId)
                .removeValue()
                .addOnSuccessListener(unused -> {

                    Toast.makeText(this, "User deleted successfully",
                            Toast.LENGTH_SHORT).show();

                    editUserId.setText("");
                    editName.setText("");
                    editRole.setText("");
                })
                .addOnFailureListener(e -> {

                    Toast.makeText(this, "Delete failed: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                });
    }

    @Override
    protected void onStart() {
        super.onStart();

        databaseReference.child("users")
                .addValueEventListener(usersListener);
    }

    @Override
    protected void onStop() {
        super.onStop();

        databaseReference.child("users")
                .removeEventListener(usersListener);
    }
}