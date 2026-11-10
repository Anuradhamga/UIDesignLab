package com.example.uidesignlab;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class TaskListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_list);

        ListView listViewTasks = findViewById(R.id.listViewTasks);

        String[] tasks = {
                "Complete Android Lab 01",
                "Complete Android Lab 02",
                "Complete Android Lab 03",
                "Study Java Programming",
                "Complete Assignment",
                "Prepare Project Proposal"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                tasks
        );

        listViewTasks.setAdapter(adapter);

        listViewTasks.setOnItemClickListener((parent, view, position, id) -> {
            Toast.makeText(this,
                    tasks[position],
                    Toast.LENGTH_SHORT).show();
        });
    }
}