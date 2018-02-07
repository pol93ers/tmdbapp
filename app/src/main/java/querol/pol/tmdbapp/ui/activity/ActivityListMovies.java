package querol.pol.tmdbapp.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import querol.pol.tmdbapp.R;
import querol.pol.tmdbapp.base.activity.ActivityBase;
import querol.pol.tmdbapp.ui.fragment.FragmentListMovies;
import querol.pol.tmdbapp.util.FragmentHelper;

/**
 * Created by Pol Querol on 5/2/18.
 */

public class ActivityListMovies extends ActivityBase {

    @NonNull
    @Override
    public ID getComponent() {
        return ID.ActivityListMovies;
    }

    public static Intent newStartIntent(Context context) {
        return new Intent(context, ActivityListMovies.class);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_list_movies);

        if (savedInstanceState == null) {
            FragmentHelper helper = getFragmentHelper();
            helper.commit(helper.replace(
                    null,
                    new FragmentHelper.Target<>(
                            R.id.activity_content,
                            FragmentListMovies.newInstance()
                    )
            ));
        }
    }
}
