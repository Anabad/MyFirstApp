package com.example.anand.myfirstapp.realm;

import com.example.anand.myfirstapp.model.Movie;

import java.util.Date;

import io.realm.Realm;

/**
 * Created by anand on 13/10/16.
 */

public class RealmInitialData implements Realm.Transaction {

    @Override
    public void execute (Realm realm){
        Movie movie = new Movie();

        movie.setId(1);
        movie.setTitle("Test Movie");
        movie.setPopularity(0.0);
        movie.setReleaseDate(new Date());
        realm.insertOrUpdate(movie);
    }

    @Override
    public int hashCode() {
        return RealmInitialData.class.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null && obj instanceof RealmInitialData;
    }
}
