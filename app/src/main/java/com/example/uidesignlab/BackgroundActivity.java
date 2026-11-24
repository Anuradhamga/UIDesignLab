
        package com.example.uidesignlab;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BackgroundActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private TextView txtStatus;

    private Button btnStartHeavy;
    private Button btnFreezeUi;
    private Button btnStartService;
    private Button btnStartLeaky;

    private ExecutorService executorService;
    private Handler mainHandler;

    private boolean isRunning = false;
    private boolean leakDemoActive = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_background);

        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.main), (v, insets) -> {

                    Insets systemBars = insets.getInsets(
                            WindowInsetsCompat.Type.systemBars());

                    v.setPadding(
                            systemBars.left,
                            systemBars.top,
                            systemBars.right,
                            systemBars.bottom);

                    return insets;
                });

        progressBar = findViewById(R.id.progressBar);
        txtStatus = findViewById(R.id.txtStatus);

        btnStartHeavy = findViewById(R.id.btnStartHeavy);
        btnFreezeUi = findViewById(R.id.btnFreezeUi);
        btnStartService = findViewById(R.id.btnStartService);
        btnStartLeaky = findViewById(R.id.btnStartLeaky);

        executorService = Executors.newSingleThreadExecutor();

        mainHandler = new Handler(Looper.getMainLooper());

        btnStartHeavy.setOnClickListener(v -> startBackgroundTask());

        btnFreezeUi.setOnClickListener(v -> freezeUI());

        btnStartService.setOnClickListener(v -> startDownloadService());

        btnStartLeaky.setOnClickListener(v -> toggleMemoryLeakDemo());
    }

    // Background Thread Task
    private void startBackgroundTask() {

        if (isRunning) {
            return;
        }

        isRunning = true;

        progressBar.setProgress(0);
        progressBar.setVisibility(ProgressBar.VISIBLE);

        btnStartHeavy.setEnabled(false);

        txtStatus.setText("Background Task Started...");

        executorService.execute(() -> {

            for (int i = 1; i <= 5; i++) {

                try {
                    Thread.sleep(1000);

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }

                final int progress = i;

                mainHandler.post(() -> {

                    progressBar.setProgress(progress);

                    txtStatus.setText(
                            "Background Progress: " + progress + "/5");
                });
            }

            mainHandler.post(() -> {

                txtStatus.setText("Background Task Completed!");

                btnStartHeavy.setEnabled(true);

                progressBar.setVisibility(ProgressBar.INVISIBLE);

                isRunning = false;

                Toast.makeText(
                        BackgroundActivity.this,
                        "Task Completed Successfully!",
                        Toast.LENGTH_SHORT).show();
            });
        });
    }

    // Demonstrate Main Thread Blocking
    private void freezeUI() {

        txtStatus.setText("UI is Freezing...");

        btnFreezeUi.setEnabled(false);

        Toast.makeText(
                this,
                "Main Thread Blocked for 5 Seconds!",
                Toast.LENGTH_SHORT).show();

        try {
            Thread.sleep(5000);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        txtStatus.setText("UI is Active Again!");

        btnFreezeUi.setEnabled(true);
    }

    // Start Download Service
    private void startDownloadService() {

        Intent serviceIntent = new Intent(
                BackgroundActivity.this,
                DownloadService.class
        );

        startService(serviceIntent);

        Toast.makeText(
                this,
                "Download Service Started!",
                Toast.LENGTH_SHORT
        ).show();
    }

    // Demonstrate and Fix Memory Leak
    private void toggleMemoryLeakDemo() {

        if (!leakDemoActive) {

            MemoryLeakDemo.start(this);

            leakDemoActive = true;

            btnStartLeaky.setText("Fix Memory Leak");

            txtStatus.setText("Potential Memory Leak Demonstrated!");

            Toast.makeText(
                    this,
                    "Activity Reference Stored!",
                    Toast.LENGTH_SHORT
            ).show();

        } else {

            MemoryLeakDemo.clear();

            leakDemoActive = false;

            btnStartLeaky.setText("Start Leaky Task");

            txtStatus.setText("Memory Leak Fixed!");

            Toast.makeText(
                    this,
                    "Activity Reference Cleared!",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    @Override
    protected void onDestroy() {

        // Always clear the demo reference before destroying Activity
        MemoryLeakDemo.clear();

        if (executorService != null) {
            executorService.shutdownNow();
        }

        if (mainHandler != null) {
            mainHandler.removeCallbacksAndMessages(null);
        }

        super.onDestroy();
    }
}
