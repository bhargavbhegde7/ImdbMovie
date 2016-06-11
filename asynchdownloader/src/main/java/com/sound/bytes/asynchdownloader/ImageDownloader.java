package com.sound.bytes.asynchdownloader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.InputStream;
import java.net.URL;

/**
 * Created by goodbytes on 6/11/2016.
 */
public class ImageDownloader extends AsyncTask<String, String, Bitmap> {

    Bitmap bitmap;
    ImageDataHandler handler;
    Context appCtxt;

    public ImageDownloader(ImageDataHandler handlerFromMainActivity, Context mainAppCtxt){
        handler = handlerFromMainActivity;
        appCtxt = mainAppCtxt;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }
    protected Bitmap doInBackground(String... args) {
        try {
            bitmap = BitmapFactory.decodeStream((InputStream) new URL(args[0]).getContent());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }
    protected void onPostExecute(Bitmap image) {
        if(image != null){
            handler.onImageDownloadCompleted(image,appCtxt);
        }else{
            Toast.makeText(appCtxt, "Image Does Not exist or Network Error", Toast.LENGTH_SHORT).show();
        }
    }
}//LoadImage class ends