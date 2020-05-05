package com.example.bookse;


import android.os.AsyncTask;
import android.widget.TextView;

import com.example.bookse.ui.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

public class FetchBook extends AsyncTask<String, Void, String> {
    private TextView mTitleText;
    private TextView mAuthorText;

    public FetchBook(TextView mTitleText, TextView mAuthorText) {
        this.mTitleText = mTitleText;
        this.mAuthorText = mAuthorText;
    }

    @Override
    protected String doInBackground(String... strings) {
        return NetworkUtils.getBookInfo(strings[0]);
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        try {
            JSONObject jsonObject = new JSONObject(s);
            JSONArray itemsArray = jsonObject.getJSONArray("items");

            for (int i = 0; i < itemsArray.length(); i++) {
                JSONObject book = itemsArray.getJSONObject(i);
                String title = null;
                String authors = null;
                JSONObject volumeInfo = book.getJSONObject("volumeInfo");

                try {
                    title = volumeInfo.getString("title");
                    authors = volumeInfo.getString("authors");
                } catch (Exception e) {
                    e.printStackTrace();
                }

                //If both a title and  author exist, update the TextView and return
                if (title != null && authors != null) {
                    mTitleText.setText(title);
                    mAuthorText.setText(authors);
                    return;
                }
            }

            mTitleText.setText("No Results Found");
            mAuthorText.setText("");
        } catch (Exception e) {
            mTitleText.setText("No results found");
            mAuthorText.setText("");
            e.printStackTrace();
        }
    }
}

