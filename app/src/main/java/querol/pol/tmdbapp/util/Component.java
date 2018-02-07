package querol.pol.tmdbapp.util;

import android.support.annotation.NonNull;

/**
 * Created by Pol Querol on 5/2/18.
 */

public interface Component {
    enum ID{
        Application("ApplicationTMDBApp"),

        ActivitySplash("ActivitySplash"),
        ActivityListMovies("ActivityListMovies"),

        FragmentListMovies("FragmentListMovies"),

        AdapterListMovies("AdapterListMovies")
        ;

        private String id;
        ID(String id){
            this.id = id;
        }
        public String id(){
            return id;
        }

        @Override
        public String toString() {
            return id();
        }
    }
    @NonNull ID getComponent();
}
