package com.example.opencv_import_3_4_5;

import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraX;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.Preview;
import androidx.camera.core.PreviewConfig;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.Toast;

import java.util.concurrent.ExecutionException;

public class OGCamera extends AppCompatActivity {

    // This is an arbitrary number we are using to keep track of the permission
    // request. Where an app has multiple context for requesting permission,
    // this can help differentiate the different contexts.
    private static final int PERMISSION_ALL = 0;
    private String TAG = "OGCameraTag";
    private boolean permissionGranted = false;
    private TextureView textureView;
    //private PreviewView previewView;
    //private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private ImageCapture imageCapture;

    // This is an array of all the permission specified in the manifest.
    private String[] PERMISSIONS = {
        Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public void showToast(final String toast)
    {
        runOnUiThread(() -> Toast.makeText(OGCamera.this, toast, Toast.LENGTH_SHORT).show());
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_o_g_camera);

        checkPermissions();

        textureView = (TextureView) findViewById(R.id.view_finder);
        //previewView = (PreviewView) findViewById(R.id.preview_view);

        if(permissionGranted = true){
            /*
            cameraProviderFuture = ProcessCameraProvider.getInstance(this);
            cameraProviderFuture.addListener(() -> {
                try {
                    ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                    bindPreview(cameraProvider);
                } catch (ExecutionException | InterruptedException e) {
                    // No errors need to be handled for this Future.
                    // This should never be reached.
                }
            }, ContextCompat.getMainExecutor(this));
            */
            textureView.post(startCamera());
        } else {
            showToast("Please accept permissions for camera use.");
        }

        textureView.addOnLayoutChangeListener(updateTransform());
    }

    private Preview.OnPreviewOutputUpdateListener listener(){
        ViewGroup parent = (ViewGroup) textureView.getParent();
        parent.removeView(textureView);
        parent.addView(textureView, 0);

        //textureView.setSurfaceTexture(new SurfaceTexture(textureView));

        updateTransform();

        return null;
    }

    private java.lang.Runnable startCamera(){
        PreviewConfig previewConfig = new PreviewConfig.Builder().setTargetResolution(new Size(640, 480)).build();

        Preview preview = new Preview(previewConfig);

        preview.setOnPreviewOutputUpdateListener(listener());

        CameraX.bindToLifecycle(this, preview);

        return null;
        /*
        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        Camera camera = cameraProvider.bindToLifecycle((LifecycleOwner)this, cameraSelector, preview);

        preview.setSurfaceProvider(previewView.createSurfaceProvider(camera.getCameraInfo()));
        */
    }

    private View.OnLayoutChangeListener updateTransform() {
        Matrix matrix = new Matrix();

        float centerX = textureView.getWidth() / 2f;
        float centerY = textureView.getHeight() / 2f;

        int rotationDegrees = 0;

        switch(textureView.getDisplay().getRotation()) {
            case Surface.ROTATION_0: rotationDegrees = 0; break;
            case Surface.ROTATION_90: rotationDegrees = 90; break;
            case Surface.ROTATION_180: rotationDegrees = 180; break;
            case Surface.ROTATION_270: rotationDegrees = 270; break;
            default: break;
        };

        matrix.postRotate((float)-rotationDegrees, centerX, centerY);

        textureView.setTransform(matrix);

        return null;
    }
    /*
    void bindPreview(@NonNull ProcessCameraProvider cameraProvider) {
        Preview preview = new Preview.Builder()
            .build();

        imageCapture = new ImageCapture.Builder()
            .setTargetRotation(previewView.getDisplay().getRotation())
            .build();

        CameraSelector cameraSelector = new CameraSelector.Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
            .build();

        Camera camera = cameraProvider.bindToLifecycle((LifecycleOwner)this, cameraSelector, preview);
        cameraProvider.bindToLifecycle((LifecycleOwner)this, cameraSelector, imageCapture, imageAnalysis, preview);

        preview.setSurfaceProvider(previewView.createSurfaceProvider(camera.getCameraInfo()));
    }

    public void onClick() {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        String currentDateandTime = sdf.format(new Date());
        String fileName = Environment.getDataDirectory().getPath() + "/sample_picture_" + currentDateandTime + ".jpg";

        ImageCapture.OutputFileOptions outputFileOptions =
                new ImageCapture.OutputFileOptions.Builder(new File(fileName)).build();
        imageCapture.takePicture();
    }

    @NonNull
    @Override
    public CameraXConfig getCameraXConfig() {
        return Camera2Config.defaultConfig();
    }*/
}
