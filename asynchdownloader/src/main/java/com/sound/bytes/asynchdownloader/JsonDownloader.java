package com.sound.bytes.asynchdownloader;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by goodbytes on 6/11/2016.
 */
public class JsonDownloader extends AsyncTask<String, Void, String> {

    JsonDataHandler handler;
    //ProgressDialog pdLoading;
    public String data;
    Context appCtxt;

    public JsonDownloader(JsonDataHandler handlerFromMainActivity, Context mainAppCtxt){
        handler = handlerFromMainActivity;
        appCtxt = mainAppCtxt;
        //pdLoading = new ProgressDialog(appCtxt);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        //this method will be running on UI thread
        //pdLoading.setMessage("\tLoading...");
        //pdLoading.show();
    }

    @Override
    protected String doInBackground(String... params) {

        try {
            data = downloadUrl(params[0]);
        } catch (IOException e) {
            System.out.println("doInBackGround exception :"+e.getMessage());
        }

        return data;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        //pdLoading.dismiss();
        handler.onJsonDownloadCompleted(result,appCtxt);
    }

    // Reads an InputStream and converts it to a String.
    public String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
        Reader reader = null;
        reader = new InputStreamReader(stream, "UTF-8");
        char[] buffer = new char[len];
        reader.read(buffer);
        return new String(buffer);
    }//readIt

    // Given a URL, establishes an HttpUrlConnection and retrieves
    // the web page content as a InputStream, which it returns as
    // a string.
    private String downloadUrl(String myurl) throws IOException {
        InputStream is = null;
        // Only display the first 500 characters of the retrieved
        // web page content.
        int len = 5000;

        try {
            URL url = new URL(myurl);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");

            conn.setDoInput(true);
            // Starts the query

            conn.connect();//culprit

            int response = conn.getResponseCode();

            is = conn.getInputStream();

            // Convert the InputStream into a string
            String contentAsString = readIt(is, len);

            return contentAsString;

            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }//downloadUrl

    /*making LoadImage class*/

}/* class made */
