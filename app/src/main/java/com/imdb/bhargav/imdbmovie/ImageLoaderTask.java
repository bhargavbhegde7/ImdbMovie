package com.imdb.bhargav.imdbmovie;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.InputStream;
import java.net.URL;

/**
 * Created by goodbytes on 5/25/2016.
 */
public class ImageLoaderTask extends AsyncTask<String, String, Bitmap> {

    Bitmap bitmap;
    ImageHandler handler;

    public ImageLoaderTask(ImageHandler handlerFromMainActivity){
        handler = handlerFromMainActivity;
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
            handler.onImageDownloadComplete(image);
        }else{
            Toast.makeText((Context) handler, "Image Does Not exist or Network Error", Toast.LENGTH_SHORT).show();
        }
    }
}//LoadImage class ends
