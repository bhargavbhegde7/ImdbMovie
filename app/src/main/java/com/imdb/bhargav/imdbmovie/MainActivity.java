package com.imdb.bhargav.imdbmovie;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.speech.RecognizerIntent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends ActionBarActivity implements MovieDataHandler, ImageHandler{

    private final int REQ_CODE_SPEECH_INPUT = 100;
    private ImageButton btnSpeak;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnSpeak = (ImageButton) findViewById(R.id.imageButton);
        btnSpeak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                promptSpeechInput();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
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

    public void bringData(View view) throws IOException {

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

        new MovieDataDownloaderTask(this).execute(url);
    }

    private String getRating(String result) throws JSONException {

        JSONObject reader = new JSONObject(result);
        String rating = reader.getString("imdbRating");
        return rating;
    }

    @Override
    public void onDataDownloadComplete(String result) {
        //this method will be running on UI thread
        TextView ratingDisplay = (TextView)findViewById(R.id.textView3);

        //parse the json
        String rating = "....";
        try {
            rating = getRating(result);
            new ImageLoaderTask(this).execute(getPosterUrl(result));
        } catch (JSONException e) {
            Toast.makeText(MainActivity.this, "The details do not exist", Toast.LENGTH_SHORT).show();
        }

        ratingDisplay.setText(rating);
    }

    private String getPosterUrl(String result) throws JSONException {

        JSONObject reader = new JSONObject(result);

        String posterUrl = reader.getString("Poster");
        posterUrl = posterUrl.replace("300","500");
        System.out.println("inside getRating  :" + posterUrl);

        return posterUrl;
    }

    /* for speech input */

    /**
     * Showing google speech input dialog
     * */
    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                "Say the movie's name");
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    "speech not supported",
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Receiving speech input
     * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                    EditText titlebox = (EditText)findViewById(R.id.editText);
                    titlebox.setText(result.get(0));
                    try {
                        bringData(titlebox);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
            }

        }
    }

    @Override
    public void onImageDownloadComplete(Bitmap image) {
        ImageView poster = (ImageView)findViewById(R.id.imageView);
        poster.setImageBitmap(image);
    }

    /* for speech input */
}
