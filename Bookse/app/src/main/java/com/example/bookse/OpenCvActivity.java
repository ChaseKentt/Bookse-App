package com.example.bookse;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.dnn.Net;
import org.opencv.imgproc.Imgproc;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OpenCvActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2 {

    private static final String TAG = "MainActivityTag";

    private static final int PERMISSION_ALL = 0;
    //CameraBridgeViewBase cameraBridgeViewBase;
    //private JavaCameraView cameraBridgeViewBase;
    private boolean isPortrait = true;
    private JavaCameraView cameraBridgeViewBase2;
    int counter = 0;
    private boolean permissionGranted = false;
    private Mat frame;
    private Bitmap bitmap;
    private ImageView imageView;
    private double bArea = 0;
    private double ratio = 0;
    private RotatedRect brRect;
    private Rect bRect;
    private Rect centerRect;
    private int bIndex = 0;
    private Point[] bPts;
    private Mat mRgbaT;
    private int width;
    private int height;
    //private Mat mGray;

    String[] PERMISSIONS = {
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        height = displayMetrics.heightPixels;
        width = displayMetrics.widthPixels;

        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            isPortrait = false;
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            isPortrait = true;
        }
    }

    @SuppressLint("SimpleDateFormat")
    public void captureImage(View view) {
        double area = 0;
        Context inContext = getApplicationContext();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        String currentDateandTime = sdf.format(new Date());
        String fileName = Environment.getDataDirectory().getPath() + "/sample_picture_" + currentDateandTime + ".jpg";
        //Toast.makeText(this, fileName + " saved", Toast.LENGTH_SHORT).show();

        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        File myPath = new File(directory,fileName);

        Log.i(TAG, "Directory: " + directory + ", File: " + fileName + ", Path: " + myPath);
        bitmap = Bitmap.createBitmap(cameraBridgeViewBase2.getWidth()/4, cameraBridgeViewBase2.getHeight()/4, Bitmap.Config.ARGB_8888);
        try{
            Mat flipped_frame = frame.t();

            Core.flip(frame.t(), flipped_frame, 1);
            Imgproc.resize(flipped_frame, flipped_frame, frame.size());

            Mat final_frame = new Mat(flipped_frame, bRect);

            //Converting frame to bitmap
            bitmap = Bitmap.createBitmap(final_frame.cols(), final_frame.rows(), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(final_frame, bitmap);
        } catch(Exception ex){
            Log.e(TAG, "Bitmap FAILURE: " + ex.getMessage());
            showToast("Exception caught");
            return;
        }

        //Saving bitmap to phone, returning Uri path in intent for other activity use
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), bitmap, fileName, null);
        Uri uriPath = Uri.parse(path);

        Intent myIntent = new Intent(view.getContext(), ViewImage.class);
        myIntent.putExtra("UriPath", uriPath);
        myIntent.putExtra("ImageName", fileName);
        startActivityForResult(myIntent, 0);
    }

    public void showToast(final String toast)
    {
        Toast.makeText(OpenCvActivity.this, toast, Toast.LENGTH_SHORT).show();
    }

    private BaseLoaderCallback baseLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case BaseLoaderCallback.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");
                    cameraBridgeViewBase2.enableView();
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG,"Beginning of on create");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_opencv);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread paramThread, Throwable paramThrowable) {
                Log.e(TAG,"Exception caught: whoops...");
                showToast("Exception caught");
            }
        });

        checkPermissions();

        if (permissionGranted) {
            Log.i(TAG, "Permissions granted, setting up camera...");

            setContentView(R.layout.activity_main_opencv);
            cameraBridgeViewBase2 = (JavaCameraView) findViewById(R.id.CameraView1);
            cameraBridgeViewBase2.setVisibility(SurfaceView.VISIBLE);
            cameraBridgeViewBase2.setCvCameraViewListener(this);
        }
        else {
            Log.i(TAG, "Permissions not granted, asking...");
            showToast("Please grant permissions");
        }
    }

    public boolean hasPermissions(){
        for (String permission : PERMISSIONS) {
            if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }

        return true;
    }

    public void checkPermissions(){
        Log.i(TAG, "Inside checking permissions");
        if (!hasPermissions()) {
            Log.i(TAG, "Not all permissions granted");
            // Permission is not granted
            // No explanation needed; request the permission
            ActivityCompat.requestPermissions(this,
                    PERMISSIONS,
                    PERMISSION_ALL);

            // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
            // app-defined int constant. The callback method gets the
            // result of the request.
        }
        else {
            Log.i(TAG, "Permissions have been granted");
            permissionGranted = true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_ALL: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    showToast("Permissions needed for app to work!");
                } else {
                    permissionGranted = true;
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        int x = 0;
        int y = 0;
        int bwidth;
        int bheight;
        bArea = 0;

        frame = inputFrame.rgba();

        centerRect.width = frame.width();
        centerRect.height = frame.height();

        mRgbaT = frame.t();

        Core.flip(frame.t(), mRgbaT, 1);
        Imgproc.resize(mRgbaT, mRgbaT, frame.size());
        bwidth = 350;
        bheight = 450;

        x = (int) (centerRect.tl().x + centerRect.br().x)/2;
        y = (int) (centerRect.tl().y + centerRect.br().y)/2;
        bRect = new Rect(x - bwidth / 2,y - bheight / 2,bwidth,bheight);

        Imgproc.rectangle(mRgbaT, new Point(bRect.x, bRect.y), new Point(bRect.x + bRect.width, bRect.y + bRect.height), new Scalar(255, 0, 0, 255), 3);

        return mRgbaT;
    }


    @Override
    public void onCameraViewStarted(int width, int height) {
        frame = new Mat(height, width, CvType.CV_8UC4);
        centerRect = new Rect();
    }


    @Override
    public void onCameraViewStopped() {}

    @Override
    protected void onResume() {
        super.onResume();

        if (!OpenCVLoader.initDebug()){
            showToast("There's a problem, yo!");
        }
        else
        {
            baseLoaderCallback.onManagerConnected(baseLoaderCallback.SUCCESS);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(cameraBridgeViewBase2!=null){
            cameraBridgeViewBase2.disableView();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cameraBridgeViewBase2!=null){
            cameraBridgeViewBase2.disableView();
        }
    }
}