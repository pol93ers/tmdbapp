package querol.pol.tmdbapp.ui.activity;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import querol.pol.tmdbapp.R;
import querol.pol.tmdbapp.base.activity.ActivityBase;
import querol.pol.tmdbapp.util.AnimationHelper;
import querol.pol.tmdbapp.util.ResourcesUtil;

/**
 * Created by Pol Querol on 5/2/18.
 */

public class ActivitySplash extends ActivityBase{
    @NonNull
    @Override
    public ID getComponent() {
        return ID.ActivitySplash;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        View appLogo = findViewById(R.id.app_logo);

        ObjectAnimator alphaAnimator = AnimationHelper.createOfAlpha(appLogo, true);
        alphaAnimator.setDuration(ResourcesUtil.getInt(this, R.integer.anim_millisTime_splash));
        alphaAnimator.addListener(new AnimationHelper.AnimationEndListener() {
            @Override
            public void onAnimationEnd(Animator animation) {
                startListMoviesActivity();
            }
        });
        AnimationHelper.startAnimator(alphaAnimator, false);
    }

    private void startListMoviesActivity() {
        startActivityAndFinish(
                ActivityListMovies.newStartIntent(this)
        );
    }
}
