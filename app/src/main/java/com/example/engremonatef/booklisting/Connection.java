package com.example.engremonatef.booklisting;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Eng.Remon Atef on 7/12/2017.
 */

public class Connection extends AsyncTaskLoader<String> {

    private String editTextData;

    public Connection(Context context, String editTextData) {
        super(context);
        this.editTextData = editTextData;
    }

    @Override
    public String loadInBackground() {

        String URL = "https://www.googleapis.com/books/v1/volumes?q=";
        String Query = URL + editTextData;
        StringBuilder result = new StringBuilder();
        URL url = null;
        HttpURLConnection httpURLConnection = null;
        int responseCode;
        try {
            url = new URL(Query);
            int timeOut = 10000;
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            httpURLConnection.setRequestProperty("Accept", "application/json");
            httpURLConnection.setRequestProperty("X-Android-Package", "com.example.engremonatef.booklisting");
            httpURLConnection.setRequestProperty("X-Android-Cert", "DA:61:80:27:A5:C6:C5:64:B3:5D:82:D7:27:EE:9F:3D:2D:72:E0:B8");
            httpURLConnection.setReadTimeout(timeOut);
            httpURLConnection.setConnectTimeout(timeOut);
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.connect();

            responseCode = httpURLConnection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
            }
        } catch (MalformedURLException e) {
            Log.d("TEST", "MalformedURLException");
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("TEST", "Connection Failed");
        } finally {
            httpURLConnection.disconnect();
        }


        return result.toString();
    }
}
