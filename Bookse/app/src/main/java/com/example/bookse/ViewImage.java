package com.example.bookse;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;

import java.io.IOException;
import java.util.List;

public class ViewImage extends AppCompatActivity {

    private final String TAG = "ViewImageTag";
    private String searchingString = "";
    private TextView mTextView;
    private TextView mAuthorText, mTitleText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_image);

        mTextView = findViewById(R.id.textView);
        mAuthorText = (TextView) findViewById(R.id.authorText);
        mTitleText = (TextView) findViewById(R.id.titleText);

        Intent intent = getIntent();
        Uri uriPath = intent.getParcelableExtra("UriPath");
        String filename = intent.getStringExtra("ImageName");

        if(uriPath != null){
            try {
                Bitmap b = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uriPath);

                runTextRecognition(b);

                ImageView img = findViewById(R.id.imageView);
                img.setImageBitmap(b);
            } catch (IOException e) {
                Log.i(TAG, "Open image exception");
                e.printStackTrace();
            }
        }
    }

    private void runTextRecognition(Bitmap b) {
        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(b);
        FirebaseVisionTextRecognizer recognizer = FirebaseVision.getInstance()
                .getOnDeviceTextRecognizer();
        recognizer.processImage(image)
                .addOnSuccessListener(
                        new OnSuccessListener<FirebaseVisionText>() {
                            @Override
                            public void onSuccess(FirebaseVisionText texts) {
                                processTextRecognitionResult(texts);
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Task failed with an exception
                                e.printStackTrace();
                            }
                        });
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void processTextRecognitionResult(FirebaseVisionText results) {
        List<FirebaseVisionText.TextBlock> blocks = results.getTextBlocks();
        if (blocks.size() == 0) {
            showToast("No text found");
            return;
        }

        double avgSize=0,totalSize=0,totalWord=0;
        for (int i = 0; i < blocks.size(); i++) {
            List<FirebaseVisionText.Line> lines = blocks.get(i).getLines();
            for (int j = 0; j < lines.size(); j++) {
                List<FirebaseVisionText.Element> elements = lines.get(j).getElements();
                for (int k = 0; k < elements.size(); k++) {
                    Rect elementFrame = elements.get(k).getBoundingBox();
                    double size = (elementFrame.bottom - elementFrame.top)*(elementFrame.right-elementFrame.left);
                    totalSize = totalSize + size;
                    totalWord = totalWord + 1;
                }
            }
        }
        avgSize = totalSize/totalWord;

        for (int i = 0; i < blocks.size(); i++) {
            List<FirebaseVisionText.Line> lines = blocks.get(i).getLines();
            for (int j = 0; j < lines.size(); j++) {
                List<FirebaseVisionText.Element> elements = lines.get(j).getElements();
                for (int k = 0; k < elements.size(); k++) {
                    String elementText = elements.get(k).getText();
                    Rect elementFrame = elements.get(k).getBoundingBox();
                    double size = (elementFrame.bottom - elementFrame.top)*(elementFrame.right-elementFrame.left);

                    if(size >= avgSize)
                        searchingString = searchingString + elementText + " ";
                }
            }
        }
        mTextView.append(searchingString);

        InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        //inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

        //For checking the network state and empty search field case
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if(networkInfo!=null && networkInfo.isConnected() && searchingString.length()!=0){
            new FetchBook(mTitleText, mAuthorText).execute(searchingString);
            mAuthorText.setText("");
            //mTitleText.setText(R.string.loading);
        }
        else {
            if(searchingString.length() == 0) {
                mAuthorText.setText("");
                mTitleText.setText("");
                Toast.makeText(this, "Please enter a search term.", Toast.LENGTH_SHORT).show();
            }
            else {
                mAuthorText.setText("");
                mTitleText.setText("");
                Toast.makeText(this, "Please check your network connection and try again.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}