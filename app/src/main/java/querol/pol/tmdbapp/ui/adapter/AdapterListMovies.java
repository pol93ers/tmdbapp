package querol.pol.tmdbapp.ui.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import querol.pol.tmdbapp.R;
import querol.pol.tmdbapp.base.adapter.AdapterRecyclerBase;
import querol.pol.tmdbapp.util.ResourcesUtil;

/**
 * Created by Pol Querol on 6/2/18.
 */

public class AdapterListMovies<I extends AdapterListMovies.MovieListItem>
        extends AdapterRecyclerBase<I, AdapterRecyclerBase.BindableViewHolder<I>>
{
    @NonNull
    @Override
    public ID getComponent() {
        return ID.AdapterListMovies;
    }

    public enum VIEW_TYPE {
        Movie(1, R.layout.adapter_movie_item);

        private final int value;
        private @LayoutRes
        final int resLayout;
        VIEW_TYPE(int value, @LayoutRes int resLayout) {
            this.value = value;
            this.resLayout = resLayout;
        }
        public int val() {
            return value;
        }
        public @LayoutRes int resLayout() {
            return resLayout;
        }

        public static @NonNull
        VIEW_TYPE fromInt(int value) {
            for (VIEW_TYPE val: VIEW_TYPE.values()) {
                if (val.val() == value) {
                    return val;
                }
            }
            throw new IllegalArgumentException("No VIEW_TYPE for " + String.valueOf(value));
        }
    }

    public interface MovieListItem<
            M extends MovieListItem.Movie> {
        VIEW_TYPE getViewType();
        @NonNull M getMovie();

        interface Movie {
            int getID();
            @NonNull String getTitle();
            @NonNull String getYear();
            @NonNull String getOverview();
            Uri getUri();
        }
    }

    private ResourcesUtil.ImageLoader loader;
    public AdapterListMovies(@NonNull ResourcesUtil.ImageLoader loader) {
        this.loader = loader;
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position).getViewType().val();
    }

    @NonNull @Override
    protected BindableViewHolder<I> onCreateViewHolder(
            Context context, LayoutInflater inflater, ViewGroup parent, int viewType
    ) {
        VIEW_TYPE type = VIEW_TYPE.fromInt(viewType);
        return new BindableViewHolder<>(inflater.inflate(type.resLayout(), parent, false));
    }

    @Override
    public void onBindViewHolder(final BindableViewHolder<I> holder, final int position) {
        super.onBindViewHolder(holder, position);
        VIEW_TYPE type = VIEW_TYPE.fromInt(holder.getItemViewType());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        switch (type) {
            case Movie:
                loader.loadImage(
                        (ImageView)holder.itemView.findViewById(R.id.photoMovieImageView),
                        holder.item.getMovie().getUri()
                );
                ((TextView)holder.itemView.findViewById(R.id.titleMovieTextView)).setText(holder.item.getMovie().getTitle());
                try {
                    Date dateMovie = dateFormat.parse(holder.item.getMovie().getYear());
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(dateMovie);
                    ((TextView)holder.itemView.findViewById(R.id.yearMovieTextView)).setText(String.valueOf(calendar.get(Calendar.YEAR)));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                ((TextView)holder.itemView.findViewById(R.id.overviewMovieTextView)).setText(holder.item.getMovie().getOverview());
                break;
            default:
                throw new IllegalArgumentException("Missing implementation");
        }
    }
}
