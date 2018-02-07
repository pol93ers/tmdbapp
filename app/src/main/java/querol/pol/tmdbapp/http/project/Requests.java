package querol.pol.tmdbapp.http.project;

/**
 * Created by Pol Querol on 5/2/18.
 */

public class Requests {
    private static Requests ourInstance = new Requests();
    public static Requests getInstance() {
        return ourInstance;
    }

    public static String getApiUrl() {
        return "https://api.themoviedb.org/3";
    }

    public static String getImageUrl(){
        return "https://image.tmdb.org/t/p/w500";
    }

    public enum Values {
        GET_POPULAR_MOVIES("getPopularMovies", getApiUrl() + "/movie/popular"),
        GET_SEARCH_MOVIE("getSearchMovie", getApiUrl() + "/search/movie")
        ;
        public final String id;
        public String where;
        Values(String id, String where) {
            this.id = id;
            this.where = where;
        }
    }
}
