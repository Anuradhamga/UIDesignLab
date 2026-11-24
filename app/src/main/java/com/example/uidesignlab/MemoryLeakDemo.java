
        package com.example.uidesignlab;

import android.app.Activity;
import android.util.Log;

public class MemoryLeakDemo {

    private static final String TAG = "MemoryLeakDemo";

    private static Activity activityReference;

    // Demonstrate a possible memory leak
    public static void start(Activity activity) {

        activityReference = activity;

        Log.d(TAG, "Activity reference stored.");
        Log.d(TAG, "Potential Memory Leak Demonstrated!");
    }

    // Check whether Activity is being held
    public static boolean isLeaking() {
        return activityReference != null;
    }

    // Clear Activity reference
    public static void clear() {

        activityReference = null;

        Log.d(TAG, "Activity reference cleared.");
        Log.d(TAG, "Memory Leak Prevention Completed!");
    }
}