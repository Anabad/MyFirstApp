package com.example.anand.myfirstapp.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.example.anand.myfirstapp.R;
import com.example.anand.myfirstapp.model.Movie;
import com.example.anand.myfirstapp.realm.RealmManager;

import java.util.Calendar;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity {
    public final static String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";

    TextView searchTextView;
    TextView titleTextView;
    TextView popularityTextView;
    TextView releaseTextView;
    ViewGroup layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Realm.init(getApplicationContext());
        RealmManager.initializeRealmConfig();
        RealmManager.incrementCount();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Realm realm = RealmManager.getRealm();
        RealmQuery<Movie> query = realm.where(Movie.class).equalTo("id",(long)realm.where(Movie.class).max("id"));
        RealmResults<Movie> results = query.findAll();
        Movie movie = results.get(0);

        titleTextView = new TextView(this);
        titleTextView.setTextSize(40);
        popularityTextView = new TextView(this);
        popularityTextView.setTextSize(40);
        releaseTextView = new TextView(this);
        releaseTextView.setTextSize(40);

        titleTextView.setText("Title: " + movie.getTitle());
        popularityTextView.setText("Popularity: " + movie.getPopularity());
        Calendar cal = Calendar.getInstance();
        cal.setTime(movie.getReleaseDate());
        releaseTextView.setText("Release year: " + cal.get(Calendar.YEAR));

        ViewGroup layout = (ViewGroup) findViewById(R.id.activity_main);
        layout.addView(titleTextView);
        layout.addView(popularityTextView);
        layout.addView(releaseTextView);



    }

    /** Called when the user clicks the Send button */
    public void searchMovie(View view) {
        Intent intent = new Intent(this, DisplayMovieActivity.class);
        EditText editText = (EditText) findViewById(R.id.movie_title);
        String message = editText.getText().toString();
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }

    @Override
    public void onResume()
    {  // After a pause OR at startup
        super.onResume();

        Realm realm = RealmManager.getRealm();
        RealmQuery<Movie> query = realm.where(Movie.class).equalTo("id",(long)realm.where(Movie.class).max("id"));
        RealmResults<Movie> results = query.findAll();
        Movie movie = results.get(0);

        titleTextView.setText("Title: " + movie.getTitle());
        popularityTextView.setText("Popularity: " + movie.getPopularity());
        Calendar cal = Calendar.getInstance();
        cal.setTime(movie.getReleaseDate());
        releaseTextView.setText("Release year: " + cal.get(Calendar.YEAR));
    }

    public void onDestroy(){
        RealmManager.decrementCount();
        super.onDestroy();
    }

}
