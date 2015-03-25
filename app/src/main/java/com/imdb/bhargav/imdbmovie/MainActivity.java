package com.imdb.bhargav.imdbmovie;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;


public class MainActivity extends ActionBarActivity {

    //public static String data = "no data";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private Drawable resize(Drawable image) {
        Bitmap b = ((BitmapDrawable)image).getBitmap();
        Bitmap bitmapResized = Bitmap.createScaledBitmap(b, 80, 100, false);
        return new BitmapDrawable(getResources(), bitmapResized);
    }

    public void printCrap(View view) throws IOException {

        ImageView imgv = (ImageView)findViewById(R.id.imageView);
        Resources res = getResources(); // need this to fetch the drawable
        Drawable draw = res.getDrawable( R.drawable.loading );

        draw = resize(draw);

        imgv.setImageDrawable(draw);


        EditText titlebox = (EditText)findViewById(R.id.editText);

        String title = titlebox.getText().toString();

        InputMethodManager imm = (InputMethodManager)getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(titlebox.getWindowToken(), 0);

        TextView titleDisplay = (TextView)findViewById(R.id.textView2);
        titleDisplay.setText(title);

        title = title.replaceAll(" ", "%20");

        String url = "http://www.omdbapi.com/?t="+title;

        new GetMovieData().execute(url);
    }

    private String getRating(String result) throws JSONException {

        //String rating = "...";
        //result = "{'sys':'hello'}";

        JSONObject reader = new JSONObject(result);
        //System.out.println("inside getRating   1  :"+rating);
        String rating = reader.getString("imdbRating");
        //System.out.println("inside getRating  :"+rating);

        return rating;
    }

    /* making class */

    private class GetMovieData extends AsyncTask<String, Void, String> {

        public String data = null;
        ProgressDialog pdLoading = new ProgressDialog(MainActivity.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //this method will be running on UI thread
            pdLoading.setMessage("\tLoading...");
            pdLoading.show();
        }

        @Override
        protected String doInBackground(String... params) {

            //System.out.println("came to doInBackGround  :"+params[0]);
            //String data = null;

            try {
                data = downloadUrl(params[0]);
                //System.out.println("doInBackGround  :"+data);
            } catch (IOException e) {
                //System.out.println("doInBackGround exception :"+e.getMessage());
            }

            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            //this method will be running on UI thread
            TextView ratingDisplay = (TextView)findViewById(R.id.textView3);

            //parse the json
            String rating = "....";
            try {
                rating = getRating(result);
                new LoadImage().execute(getPosterUrl(result));
            } catch (JSONException e) {
                //System.out.println("onPostExecution method while getting the parsed rating   :"+e.getMessage());
                Toast.makeText(MainActivity.this, "The details do not exist", Toast.LENGTH_SHORT).show();
            }

            ratingDisplay.setText(rating);

            pdLoading.dismiss();
        }

        private String getPosterUrl(String result) throws JSONException {

            //String rating = "...";
            //result = "{'sys':'hello'}";

            JSONObject reader = new JSONObject(result);
            //System.out.println("inside getRating   1  :"+rating);
            String posterUrl = reader.getString("Poster");
            posterUrl = posterUrl.replace("300","500");
            System.out.println("inside getRating  :"+posterUrl);

            return posterUrl;
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

        ImageView img;
        Bitmap bitmap;
        ProgressDialog pDialog;
        /*making LoadImage class*/

        private class LoadImage extends AsyncTask<String, String, Bitmap> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                //pDialog = new ProgressDialog(MainActivity.this);
                //pDialog.setMessage("Loading Image ....");
               // pDialog.show();
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
                    //imageView
                    ImageView poster = (ImageView)findViewById(R.id.imageView);
                    poster.setImageBitmap(image);
                    //img.setImageBitmap(image);
                   // pDialog.dismiss();
                }else{
                   // pDialog.dismiss();
                    Toast.makeText(MainActivity.this, "Image Does Not exist or Network Error", Toast.LENGTH_SHORT).show();
                }
            }
        }//LoadImage class ends

        /* LoadImage class made  */

    }/* class made */



}
