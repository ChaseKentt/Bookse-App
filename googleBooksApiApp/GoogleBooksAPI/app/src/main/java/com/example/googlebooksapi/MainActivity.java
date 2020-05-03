package com.example.googlebooksapi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.net.NetworkInterface;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private EditText mBookInput;
    private TextView mAuthorText, mTitleText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBookInput =(EditText) findViewById(R.id.bookInput);
        mAuthorText = (TextView) findViewById(R.id.authorText);
        mTitleText = (TextView) findViewById(R.id.titleText);

    }

    public void searchBooks(View view) {
        String queryString = mBookInput.getText().toString();
        //For Hiding the keyboard when the saerch button is clicked
        InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

        //For checking the network state and empty search field case
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if(networkInfo!=null && networkInfo.isConnected() && queryString.length()!=0){
            new FetchBook(mTitleText, mAuthorText).execute(queryString);
            mAuthorText.setText("");
            mTitleText.setText(R.string.loading);
        }
        else {
            if(queryString.length() == 0) {
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
