package com.example.uidesignlab;

import android.os.Bundle;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;
import android.view.Menu;
import android.view.MenuItem;
import android.net.Uri;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.ArrayList;
import java.util.List;

public class DashboardActivity extends AppCompatActivity
        implements HeaderFragment.OnHeaderInteractionListener {

    private static final String STORAGE_TAG = "StorageLab";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dashboard);

        androidx.appcompat.widget.Toolbar toolbar =
                findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        // Dashboard Visit Counter
        if (savedInstanceState == null) {

            SharedPreferences prefs = getPreferences(MODE_PRIVATE);

            int visits = prefs.getInt("visit_count", 0) + 1;

            SharedPreferences.Editor editor = prefs.edit();

            editor.putInt("visit_count", visits);

            boolean saved = editor.commit();

            Log.d(STORAGE_TAG,
                    "Visit count saved: " + saved + " (" + visits + ")");

            Toast.makeText(
                    this,
                    getString(R.string.visit_count, visits),
                    Toast.LENGTH_SHORT
            ).show();
        }

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        Button btnWebsite = findViewById(R.id.btnWebsite);

        btnWebsite.setOnClickListener(v -> {

            Intent browserIntent = new Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://www.google.com")
            );

            startActivity(browserIntent);
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<String> items = new ArrayList<>();

        items.add("Activity");
        items.add("Service");
        items.add("Broadcast Receiver");
        items.add("Content Provider");
        items.add("Intent");
        items.add("Fragment");
        items.add("View");
        items.add("Layout");
        items.add("Manifest");
        items.add("Adapter");
        items.add("RecyclerView");
        items.add("Menu");

        MyAdapter myAdapter = new MyAdapter(items);

        recyclerView.setAdapter(myAdapter);

        // Add Detail Fragment
        if (savedInstanceState == null) {

            FragmentManager fragmentManager =
                    getSupportFragmentManager();

            FragmentTransaction transaction =
                    fragmentManager.beginTransaction();

            DetailFragment detailFrag = new DetailFragment();

            transaction.add(
                    R.id.dynamic_fragment_container,
                    detailFrag
            );

            transaction.commit();
        }

        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.main),
                (v, insets) -> {

                    Insets systemBars = insets.getInsets(
                            WindowInsetsCompat.Type.systemBars()
                    );

                    v.setPadding(
                            systemBars.left,
                            systemBars.top,
                            systemBars.right,
                            systemBars.bottom
                    );

                    return insets;
                }
        );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.dashboard_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.menu_home) {

            Toast.makeText(
                    this,
                    "Home Selected",
                    Toast.LENGTH_SHORT
            ).show();

            return true;

        } else if (id == R.id.menu_settings) {

            Toast.makeText(
                    this,
                    "Settings Selected",
                    Toast.LENGTH_SHORT
            ).show();

            return true;

        } else if (id == R.id.action_notes) {

            Intent intent = new Intent(
                    this,
                    NotesActivity.class
            );

            startActivity(intent);

            return true;

        } else if (id == R.id.action_users) {

            Intent intent = new Intent(
                    DashboardActivity.this,
                    UsersActivity.class
            );

            startActivity(intent);

            return true;

        } else if (id == R.id.menu_logout) {

            new AlertDialog.Builder(this)
                    .setTitle("Logout")
                    .setMessage("Are you sure you want to log out?")

                    .setPositiveButton("Yes", (dialog, which) -> {

                        getSharedPreferences(
                                MainActivity.PREFS_NAME,
                                MODE_PRIVATE
                        ).edit().clear().apply();

                        finish();

                    })

                    .setNegativeButton("No", null)
                    .show();

            return true;

        } else if (id == R.id.action_swap) {

            Fragment current = getSupportFragmentManager()
                    .findFragmentById(R.id.dynamic_fragment_container);

            FragmentTransaction transaction =
                    getSupportFragmentManager().beginTransaction();

            if (current instanceof DetailFragment) {

                transaction.replace(
                        R.id.dynamic_fragment_container,
                        new InfoFragment()
                );

            } else {

                transaction.replace(
                        R.id.dynamic_fragment_container,
                        new DetailFragment()
                );
            }

            transaction.commit();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Receive data from Header Fragment
    @Override
    public void onButtonClicked(String data) {

        updateDetail(data);
    }

    // Update Detail Fragment
    private void updateDetail(String text) {

        Fragment current = getSupportFragmentManager()
                .findFragmentById(R.id.dynamic_fragment_container);

        if (current instanceof DetailFragment) {

            ((DetailFragment) current).updateTextView(text);

        } else {

            Toast.makeText(
                    this,
                    "Detail area is not showing",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }
}