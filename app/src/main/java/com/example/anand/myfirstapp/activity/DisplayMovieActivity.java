package com.example.anand.myfirstapp.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.anand.myfirstapp.R;
import com.example.anand.myfirstapp.realm.RealmManager;
import com.uwetrottmann.tmdb2.Tmdb;
import com.uwetrottmann.tmdb2.entities.Movie;
import com.uwetrottmann.tmdb2.entities.MovieResultsPage;
import com.uwetrottmann.tmdb2.services.SearchService;

import java.io.IOException;
import java.util.Calendar;

import io.realm.Realm;
import retrofit2.Call;

public class DisplayMovieActivity extends AppCompatActivity {
    private static final String API_KEY = "2d28c41e6dab17f0aa2ddb2a9cf2b8f0";

    TextView searchTextView;
    TextView titleTextView;
    TextView popularityTextView;
    TextView releaseTextView;
    ViewGroup layout;
    Realm realm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        RealmManager.incrementCount();
        this.realm = RealmManager.getRealm();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_movie);

        Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);

        searchMovie(message);

        searchTextView = new TextView(this);
        searchTextView.setTextSize(40);
        searchTextView.setText("Search: " + message);

        titleTextView = new TextView(this);
        titleTextView.setTextSize(40);

        popularityTextView = new TextView(this);
        popularityTextView.setTextSize(40);

        releaseTextView = new TextView(this);
        releaseTextView.setTextSize(40);



        layout = (ViewGroup) findViewById(R.id.activity_display_movie);
        layout.addView(searchTextView);
        layout.addView(titleTextView);
        layout.addView(popularityTextView);
        layout.addView(releaseTextView);
    }

    public void searchMovie (String title){
        Tmdb tmdb = new Tmdb(API_KEY);
        SearchService searchService = tmdb.searchService();
        Call<MovieResultsPage> movieResultsPageCall = searchService.movie(title,null,null,null,null,null,null);
        AsyncQuery asyncQuery = new AsyncQuery();
        Object[] call = {movieResultsPageCall};
        asyncQuery.execute(call);
    }

    private class AsyncQuery extends AsyncTask{

        private Object result;

        public Object getResult() {
            return result;
        }

        public void setResult(Object result) {
            this.result = result;
        }

        @Override
        protected Object doInBackground(Object[] params) {
            try{
                Call call = (Call) params[0];
                return call.execute().body();
            }catch (IOException ioe){
                System.err.println(ioe.getMessage());
            }
            return null;
        }

        protected void onPostExecute (Object body){
            MovieResultsPage movieResultsPage = (MovieResultsPage)body;
            Movie movie = movieResultsPage.results.get(0);

            titleTextView.setText("Title: " + movie.title);
            popularityTextView.setText("Popularity: " + movie.popularity);
            Calendar cal = Calendar.getInstance();
            cal.setTime(movie.release_date);
            releaseTextView.setText("Release year: " + cal.get(Calendar.YEAR));

            final com.example.anand.myfirstapp.model.Movie entity = new com.example.anand.myfirstapp.model.Movie();
            entity.setTitle(movie.title);
            entity.setPopularity(movie.popularity);
            entity.setReleaseDate(movie.release_date);
            entity.setId((long) realm.where(entity.getClass()).max("id") + 1L);
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    // This will create a new object in Realm or throw an exception if the
                    // object already exists (same primary key)
                    // realm.copyToRealm(obj);

                    // This will update an existing object with the same primary key
                    // or create a new object if an object with no primary key = 42
                    realm.copyToRealmOrUpdate(entity);
                }
            });
        }
    }

    public void onDestroy(){
        RealmManager.decrementCount();
        super.onDestroy();
    }
}
