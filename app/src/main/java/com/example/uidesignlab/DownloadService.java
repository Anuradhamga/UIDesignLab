package com.example.uidesignlab;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DownloadService extends Service {

    private static final String TAG = "DownloadService";

    private ExecutorService executorService;
    private Handler mainHandler;

    @Override
    public void onCreate() {
        super.onCreate();

        executorService = Executors.newSingleThreadExecutor();
        mainHandler = new Handler(Looper.getMainLooper());

        Log.d(TAG, "Download Service Created");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        mainHandler.post(() ->
                Toast.makeText(
                        getApplicationContext(),
                        "Download Started!",
                        Toast.LENGTH_SHORT
                ).show()
        );

        executorService.execute(() -> {

            for (int i = 1; i <= 5; i++) {

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    stopSelf(startId);
                    return;
                }

                Log.d(TAG, "Download Progress: " + (i * 20) + "%");
            }

            mainHandler.post(() ->
                    Toast.makeText(
                            getApplicationContext(),
                            "Download Completed!",
                            Toast.LENGTH_LONG
                    ).show()
            );

            stopSelf(startId);
        });

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {

        if (executorService != null) {
            executorService.shutdownNow();
        }

        Log.d(TAG, "Download Service Destroyed");

        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
