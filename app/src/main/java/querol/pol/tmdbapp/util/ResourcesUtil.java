package querol.pol.tmdbapp.util;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.IntegerRes;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.widget.ImageView;

/**
 * <p>Utility to access application resources</p>
 * <p>Created by Pol Querol on 05/02/2018.</p>
 */
public class ResourcesUtil {
    public interface ImageLoader {
        void loadImage(@NonNull ImageView view, @NonNull Uri uri);
    }

    public interface BitmapLoader{
        void loadBitmap(@NonNull ImageView view, @NonNull byte[] bytes);
    }

    public static int getInt(@NonNull Context context, @IntegerRes int resId)
    {
        return context.getResources().getInteger(resId);
    }

    public static Drawable getDrawable(@NonNull Context context, @DrawableRes int resId) {
        return ContextCompat.getDrawable(context, resId);
    }

    public static int getColor(@NonNull Context context, @ColorRes int resId) {
        return ContextCompat.getColor(context, resId);
    }

    public static ColorStateList getColorStateList(@NonNull Context context, @ColorRes int resId) {
        return ContextCompat.getColorStateList(context, resId);
    }

    // TODO: 10/11/2016 the rest (ContextCompat etc)
}
