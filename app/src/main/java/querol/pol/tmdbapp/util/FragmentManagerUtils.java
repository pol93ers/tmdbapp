package querol.pol.tmdbapp.util;

import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import querol.pol.tmdbapp.R;

/**
 * Created by Pol Querol on 5/2/18.
 */

public class FragmentManagerUtils {

    /**
     * Find a fragment by tag and of the specified class inside the manager
     * @param manager FragmentManager that will control the operation
     * @param clazz Fragment's class to search for
     * @param id Fragment's id to search for
     * @param <F> Class extending Fragment
     * @return A class of type F or null if not found
     */
    public static @Nullable
    <F extends Fragment> F findFragment(
            @NonNull FragmentManager manager, @NonNull Class <F> clazz, @NonNull Component.ID id
    ) {
        Fragment fragment = manager.findFragmentByTag(id.toString());

        if (fragment != null && fragment.getClass().equals(clazz)) {
            return clazz.cast(fragment);
        }
        return null;
    }

    /**
     * Remove fragment by tag of the specified class inside the manager
     * @param manager FragmentManager that will control the operation
     * @param clazz Fragment's class to search for and remove
     * @param id Fragment's id to search for and remove
     * @param <F> Class extending Fragment
     * @return True if removed else False
     */
    public static <F extends Fragment> boolean fragmentRemove(
            @NonNull FragmentManager manager, @NonNull Class <F> clazz, @NonNull Component.ID id
    ) {
        Fragment fragment = findFragment(manager, clazz, id);
        if (fragment != null) {
            manager.beginTransaction()
                    .remove(fragment)
                    .commit();
            manager.executePendingTransactions();
            return true;
        }
        return false;
    }

    /**
     * Remove fragment by tag (inclusively or not) and everything above it inside the manager's back stack
     * @param manager FragmentManager that will control the operation
     * @param id Fragment's id to search for and remove
     * @param inclusive True if the fragment will be also removed or only everything above it
     * @return True if removed else False
     */
    public static boolean fragmentRemoveBackStack(
            @NonNull FragmentManager manager, @NonNull Component.ID id, boolean inclusive
    ) {
        if (!inclusive) {
            return manager.popBackStackImmediate(
                    id.toString(),
                    0
            );
        } else {
            return manager.popBackStackImmediate(
                    id.toString(),
                    FragmentManager.POP_BACK_STACK_INCLUSIVE
            );
        }
    }

    /**
     * Add a fragment without UI for the manager
     * @param manager FragmentManager that will control the operation
     * @param fragment Fragment to be added
     * @param <F> Class extending Fragment and implementing ComponentType
     */
    public static <F extends Fragment & Component> void fragmentCall(
            @NonNull FragmentManager manager, @NonNull F fragment
    ) {
        manager.beginTransaction()
                .add(fragment, fragment.getComponent().toString())
                .commit();
        manager.executePendingTransactions();
    }

    /**
     * Remove and add a fragment into the container, animated (or not),
     * and saved in the back stack (or not) for the manager
     * @param manager FragmentManager that will control the operation
     * @param containerResourceId ViewGroup's id to add the fragment UI
     * @param fragment Fragment to be added
     * @param animate If fragment will have enter and exit animations
     * @param save If fragment will be saved to the manager's back stack
     * @param <F> Class extending Fragment adn Implementing ComponentType
     */
    public static <F extends Fragment & Component> void fragmentReplace(
            @NonNull FragmentManager manager, @IdRes int containerResourceId,
            @NonNull F fragment, boolean animate, boolean save
    ) {
        FragmentTransaction transaction = manager.beginTransaction();
        if (animate) {
            transaction.setCustomAnimations(
                    R.anim.slide_in_right,
                    R.anim.slide_out_left,
                    android.R.anim.slide_in_left,
                    android.R.anim.slide_out_right
            );
        }
        transaction.replace(
                containerResourceId,
                fragment,
                fragment.getComponent().toString()
        );
        if (save) {
            transaction.addToBackStack(
                    fragment.getComponent().toString()
            );
        }
        transaction.commit();

        manager.executePendingTransactions();
    }
}
