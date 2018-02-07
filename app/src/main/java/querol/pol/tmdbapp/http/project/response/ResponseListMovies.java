package querol.pol.tmdbapp.http.project.response;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import querol.pol.tmdbapp.data.Movie;

/**
 * Created by Pol Querol on 6/2/18.
 */

public class ResponseListMovies extends Response{
    @SerializedName("results") private ArrayList<Movie> movies;

    public ArrayList<Movie> getMovies() {
        return movies;
    }

    public void setMovies(ArrayList<Movie> movies) {
        this.movies = movies;
    }

    @Override
    public String toString() {
        return "ResponseListMovies{" +
                "movies=" + movies +
                '}';
    }
}
