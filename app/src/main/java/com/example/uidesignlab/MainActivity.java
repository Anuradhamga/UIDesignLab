package com.example.uidesignlab;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.util.Log;


public class MainActivity extends AppCompatActivity {

    public static final String PREFS_NAME = "UserPrefs";
    public static final String KEY_USERNAME = "username";
    public static final String KEY_REMEMBER = "remember";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.mainLayout), (v, insets) -> {

                    Insets systemBars = insets.getInsets(
                            WindowInsetsCompat.Type.systemBars());

                    v.setPadding(
                            systemBars.left,
                            systemBars.top,
                            systemBars.right,
                            systemBars.bottom);

                    return insets;
                });

        // Find Views
        EditText editUsername = findViewById(R.id.editUsername);
        Button btnSave = findViewById(R.id.btnSave);
        CheckBox chkRemember = findViewById(R.id.chkRemember);

        // SharedPreferences
        SharedPreferences prefs =
                getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        // Load Remembered Username
        boolean remember = prefs.getBoolean(KEY_REMEMBER, false);

        chkRemember.setChecked(remember);

        if (remember) {
            editUsername.setText(
                    prefs.getString(KEY_USERNAME, ""));
        }

        // Save Profile Button
        btnSave.setOnClickListener(v -> {

            String userName = editUsername.getText()
                    .toString().trim();

            if (userName.isEmpty()) {

                Toast.makeText(
                        MainActivity.this,
                        "Please enter username",
                        Toast.LENGTH_SHORT).show();

            } else {

                // Save Remember Me Data
                SharedPreferences.Editor editor = prefs.edit();

                if (chkRemember.isChecked()) {

                    editor.putString(KEY_USERNAME, userName);
                    editor.putBoolean(KEY_REMEMBER, true);

                } else {

                    editor.remove(KEY_USERNAME);
                    editor.putBoolean(KEY_REMEMBER, false);

                }

                editor.apply();
                final String nameToSave = userName;

                AppDatabase.databaseExecutor.execute(() -> {

                    long id = AppDatabase.getInstance(getApplicationContext())
                            .userDao()
                            .insert(new UserRecord(nameToSave));

                    Log.d("RoomLab", "Inserted " + nameToSave + ", id = " + id);

                });

                // Open Dashboard
                Intent intent = new Intent(
                        MainActivity.this,
                        DashboardActivity.class);

                intent.putExtra("username", userName);

                startActivity(intent);

                // Success Toast
                Toast.makeText(
                        MainActivity.this,
                        "Profile Saved: " + userName,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}