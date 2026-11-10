package com.example.uidesignlab;

import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import android.database.Cursor;
import android.util.Log;
import android.widget.Toast;

public class UsersActivity extends AppCompatActivity {

    private RecyclerView recyclerViewUsers;
    private UserAdapter adapter;

    private Button btnAddJohn;
    private Button btnClearAll;
    private Button btnReadProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        recyclerViewUsers = findViewById(R.id.recyclerViewUsers);

        btnAddJohn = findViewById(R.id.btnAddJohn);
        btnClearAll = findViewById(R.id.btnClearAll);
        btnReadProvider = findViewById(R.id.btnReadProvider);

        recyclerViewUsers.setLayoutManager(
                new LinearLayoutManager(this)
        );

        adapter = new UserAdapter(new ArrayList<>());
        recyclerViewUsers.setAdapter(adapter);

        btnAddJohn.setOnClickListener(v -> {
            AppDatabase.databaseExecutor.execute(() -> {

                AppDatabase.getInstance(getApplicationContext())
                        .userDao()
                        .insert(new UserRecord("John"));

                loadUsers();
            });
        });

        btnClearAll.setOnClickListener(v -> {
            AppDatabase.databaseExecutor.execute(() -> {

                AppDatabase.getInstance(getApplicationContext())
                        .userDao()
                        .deleteAll();

                loadUsers();
            });
        });
        btnReadProvider.setOnClickListener(v ->

                AppDatabase.databaseExecutor.execute(() -> {

                    int count = 0;

                    try (Cursor cursor = getContentResolver().query(
                            UserProvider.CONTENT_URI,
                            null,
                            null,
                            null,
                            null
                    )) {

                        if (cursor != null) {

                            int nameIndex =
                                    cursor.getColumnIndexOrThrow("name");

                            while (cursor.moveToNext()) {

                                String name = cursor.getString(nameIndex);

                                Log.d("RoomLab", "Provider user: " + name);

                                count++;
                            }
                        }
                    }

                    final int resultCount = count;

                    runOnUiThread(() -> Toast.makeText(
                            UsersActivity.this,
                            "Provider returned " + resultCount + " rows",
                            Toast.LENGTH_SHORT
                    ).show());

                })
        );
    }

    private void loadUsers() {

        AppDatabase.databaseExecutor.execute(() -> {

            java.util.List<UserRecord> users =
                    AppDatabase.getInstance(getApplicationContext())
                            .userDao()
                            .getAll();

            runOnUiThread(() -> adapter.setUsers(users));
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUsers();
    }
}