package querol.pol.tmdbapp.util;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.animation.AccelerateInterpolator;

import java.util.ArrayList;

import querol.pol.tmdbapp.R;


/**
 * <p>A helper class for working with ObjectAnimator and View so that animations
 * can be chained even after an animation is currently running without stopping it.</p>
 * @see <a href="https://developer.android.com/guide/topics/graphics/prop-animation.html">More info</a>
 *
 * <p>Created by Pol Querol on 05/02/2018.</p>
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class AnimationHelper {
    private static final int TAG_ANIMATION = R.id.tag_animation_helper_id;

    public static @NonNull ObjectAnimator createOfAlpha(
            @NonNull final View view, final boolean show
    ) {
        if (show) {
            return createOfAlpha(view, 0, 1);
        } else {
            return createOfAlpha(view, 1, 0);
        }
    }

    public static @NonNull ObjectAnimator createOfAlpha(
            @NonNull View view, float start, float end
    ) {
        ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(view, View.ALPHA, start, end);
        alphaAnimator.setInterpolator(new AccelerateInterpolator(1f));
        alphaAnimator.setDuration(view.getContext().getResources().getInteger(
                android.R.integer.config_mediumAnimTime
        ));
        return alphaAnimator;
    }


    public static void startAnimator(@NonNull final ObjectAnimator newAnimator, boolean forceStart) {
        View target = getTarget(newAnimator);
        ArrayList<Animator> previousAnimators = getAnimators(target);

        if (!forceStart) {
            boolean hasRunningAnimation = false;
            for (Animator previousAnimator: previousAnimators) {
                if (previousAnimator.isRunning()) {
                    hasRunningAnimation = true;
                    break;
                }
            }
            if (previousAnimators.isEmpty() || !hasRunningAnimation) {
                clearAnimators(target);
                newAnimator.start();
                addAnimator(target, newAnimator);
            } else {
                Animator previousAnimator = previousAnimators.get(previousAnimators.size()-1);
                previousAnimator.addListener(new AnimationEndListener() {
                    @Override
                    public void onAnimationEnd(Animator ignored) {
                        newAnimator.start();
                    }
                });
                addAnimator(target, newAnimator);
            }
        } else {
            clearAnimators(target);
            addAnimator(target, newAnimator);
            newAnimator.start();
        }
    }


    private static @NonNull ArrayList<Animator> getAnimators(@NonNull View view) {
        @SuppressWarnings("unchecked")
        ArrayList<Animator> animators = (ArrayList<Animator>)view.getTag(TAG_ANIMATION);
        if (animators == null) {
            animators = new ArrayList<>();
        }
        return animators;
    }

    private static void addAnimator(@NonNull View view, @NonNull Animator animator) {
        ArrayList<Animator> animators = getAnimators(view);
        animators.add(animator);
        view.setTag(TAG_ANIMATION, animators);
    }

    private static void clearAnimators(@NonNull View view) {
        ArrayList<Animator> animators = getAnimators(view);
        for (Animator anim: animators) {
            if (anim.isRunning()) anim.cancel();
        }
        view.setTag(TAG_ANIMATION, null);
    }

    private static @NonNull View getTarget(@NonNull ObjectAnimator animator) {
        View view = null;
        if (animator.getTarget() != null) {
            if (animator.getTarget() instanceof View) {
                view = (View)animator.getTarget();
            }
        }
        if (view != null) {
            return view;
        } else {
            throw new IllegalArgumentException("Animator must have Target of class View!");
        }
    }


    public static abstract class AnimationEndListener implements Animator.AnimatorListener {
        @Override public void onAnimationStart(Animator animator) {}
        @Override public void onAnimationCancel(Animator animator) {}
        @Override public void onAnimationRepeat(Animator animator) {}
    }
}
