package com.example.bookse.ui;


import android.net.Uri;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class NetworkUtils {
    private static final String LOG_TAG = NetworkUtils.class.getSimpleName();
    //Base URL for the Books API
    private static final String BOOK_BASE_URL = "https://www.googleapis.com/books/v1/volumes?";
    private static final String QUERY_PARAM ="q"; //Parameter for the search string
    private static final String MAX_RESULTS = "maxResults"; //Parameter that limits search results
    private static final String PRINT_TYPE = "printType"; //Parameter to filter by print type


    public static String getBookInfo(String queryString) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String bookJSONString = null;

        try {
            //Build  up the query URI, limiting results to 10 items and printed books
            Uri builtUri = Uri.parse(BOOK_BASE_URL).buildUpon()
                    .appendQueryParameter(QUERY_PARAM, queryString)
                    .appendQueryParameter(MAX_RESULTS, "10")
                    .appendQueryParameter(PRINT_TYPE, "books")
                    .build();

            URL requestURL = new URL(builtUri.toString());

            urlConnection = (HttpURLConnection) requestURL.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            //Read the response an inputStream and a StringBuffer, then convert to a string
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if(inputStream == null) {
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while((line = reader.readLine()) != null) {
                buffer.append(line + "\n");

            }
            if(buffer.length() == 0) {
                //Stream was empty. No point in parsing.
                return null;
            }
            bookJSONString = buffer.toString();

        } catch(Exception ex) {
            ex.printStackTrace();
            return null;

        } finally {
            if(urlConnection != null) {
                urlConnection.disconnect();
            }
            if(reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            Log.d(LOG_TAG, bookJSONString);
            return bookJSONString;
        }
    }

}


