package querol.pol.tmdbapp.data.adapter;

import android.net.Uri;
import android.support.annotation.NonNull;

import querol.pol.tmdbapp.ui.adapter.AdapterListMovies;

/**
 * <p>Item to hold AdapterListMovies data</p>
 * <p>Created by Pol Querol on 06/02/2018.</p>
 */
public class MovieListItem implements
        AdapterListMovies.MovieListItem<MovieListItem.Movie>
{

    public static class Movie implements AdapterListMovies.MovieListItem.Movie {
        private int id;
        private String title, year, overview;
        private Uri uri;

        public Movie(int id, @NonNull String title, @NonNull String year, @NonNull String overview, Uri uri) {
            this.id = id;
            this.title = title;
            this.uri = uri;
            this.year = year;
            this.overview = overview;
        }

        @Override
        public int getID() {
            return id;
        }

        @NonNull @Override
        public String getTitle() {
            return title;
        }

        @NonNull @Override
        public String getYear() {
            return year;
        }

        @NonNull @Override
        public String getOverview() {
            return overview;
        }

        @Override
        public Uri getUri() {
            return uri;
        }
    }

    private AdapterListMovies.VIEW_TYPE viewType;
    private Object item;

    public MovieListItem(@NonNull Movie photo) {
        this.viewType = AdapterListMovies.VIEW_TYPE.Movie;
        this.item = photo;
    }

    @Override
    public AdapterListMovies.VIEW_TYPE getViewType() {
        return viewType;
    }

    @NonNull @Override
    public Movie getMovie() {
        return (Movie)item;
    }
}
