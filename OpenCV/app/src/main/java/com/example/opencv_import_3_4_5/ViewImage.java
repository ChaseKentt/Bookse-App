package com.example.opencv_import_3_4_5;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;

import java.io.IOException;

public class ViewImage extends AppCompatActivity {

    private final String TAG = "ViewImageTag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_image);

        Intent intent = getIntent();
        Uri uriPath = intent.getParcelableExtra("UriPath");
        String filename = intent.getStringExtra("ImageName");

        if(uriPath != null){
            try {
                Bitmap b = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uriPath);
                ImageView img = findViewById(R.id.imageView);
                img.setImageBitmap(b);
            } catch (IOException e) {
                Log.i(TAG, "Open image exception");
                e.printStackTrace();
            }
        }
    }
}
