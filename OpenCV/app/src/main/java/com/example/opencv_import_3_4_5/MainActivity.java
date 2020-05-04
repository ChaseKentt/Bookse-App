package com.example.opencv_import_3_4_5;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivityTag";

    public void showToast(final String toast)
    {
        runOnUiThread(() -> Toast.makeText(MainActivity.this, toast, Toast.LENGTH_SHORT).show());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread paramThread, Throwable paramThrowable) {
                Log.e(TAG,"Exception caught: whoops...");
                showToast("Exception caught");
            }
        });
    }

    public void openCVView(View view) {
        Intent myIntent = new Intent(view.getContext(), OpenCVCamera.class);
        startActivityForResult(myIntent, 0);
    }
}
