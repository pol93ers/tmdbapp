package querol.pol.tmdbapp.ui.fragment;

import android.app.AlertDialog;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import querol.pol.tmdbapp.R;
import querol.pol.tmdbapp.base.fragment.FragmentBase;
import querol.pol.tmdbapp.data.Movie;
import querol.pol.tmdbapp.data.adapter.MovieListItem;
import querol.pol.tmdbapp.http.Http;
import querol.pol.tmdbapp.http.project.Requests;
import querol.pol.tmdbapp.http.project.response.Response;
import querol.pol.tmdbapp.http.project.response.ResponseError;
import querol.pol.tmdbapp.http.project.response.ResponseListMovies;
import querol.pol.tmdbapp.ui.adapter.AdapterListMovies;
import querol.pol.tmdbapp.util.ResourcesUtil;

/**
 * Created by Pol Querol on 6/2/18.
 */

public class FragmentListMovies extends FragmentBase {

    private int LIMIT_MOVIES = 20, countPagination = 1;
    private AdapterListMovies<MovieListItem> adapter;
    private boolean isDoingPetition, isSearching;
    private String querySearch;

    @NonNull
    @Override
    public ID getComponent() {
        return ID.FragmentListMovies;
    }

    public static FragmentListMovies newInstance() {
        return new FragmentListMovies();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getHttpManager().receiverRegister(getContext(), Requests.Values.GET_POPULAR_MOVIES);
        getHttpManager().receiverRegister(getContext(), Requests.Values.GET_SEARCH_MOVIE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getHttpManager().receiverUnregister(getContext());
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!getHttpManager().isCallRunning(Requests.Values.GET_POPULAR_MOVIES)){
            countPagination = 1;
            isDoingPetition = true;
            getHttpManager().callStart(
                    Http.RequestType.GET,
                    Requests.Values.GET_POPULAR_MOVIES,
                    "?api_key=" + getString(R.string.api_key) + "&page=" + countPagination,
                    null,
                    null,
                    null,
                    true
            );
        }
    }

    @Override
    public void onHttpBroadcastError(String requestId, ResponseError response) {
        super.onHttpBroadcastError(requestId, response);
        if (requestId.equals(Requests.Values.GET_POPULAR_MOVIES.id)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle(R.string.error);
            builder.setMessage(R.string.dialog_connection_error);
            builder.setPositiveButton(R.string.ok, null);
            AlertDialog dialog = builder.create();
            dialog.show();
        }else{
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle(R.string.error);
            builder.setMessage(R.string.dialog_connection_error);
            builder.setPositiveButton(R.string.ok, null);
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    @Override
    public void onHttpBroadcastSuccess(String requestId, Response response) {
        super.onHttpBroadcastSuccess(requestId, response);
        if (requestId.equals(Requests.Values.GET_POPULAR_MOVIES.id)) {
            ArrayList<Movie> response_movies = ((ResponseListMovies) response).getMovies();
            ArrayList<MovieListItem> movies = new ArrayList<>();
            int size_movies = response_movies.size();
            for (int i=0; i<size_movies; i++){
               movies.add(new MovieListItem(new MovieListItem.Movie(response_movies.get(i).getId(),
                       response_movies.get(i).getTitle(), response_movies.get(i).getYear(),
                       response_movies.get(i).getOverview(), Uri.parse(Requests.getImageUrl() + response_movies.get(i).getPhoto()))));
            }
            isDoingPetition = false;
            adapter.addItems(movies);
        }else{
            ArrayList<Movie> response_movies = ((ResponseListMovies) response).getMovies();
            ArrayList<MovieListItem> movies = new ArrayList<>();
            int size_movies = response_movies.size();
            for (int i=0; i<size_movies; i++){
                movies.add(new MovieListItem(new MovieListItem.Movie(response_movies.get(i).getId(),
                        response_movies.get(i).getTitle(), response_movies.get(i).getYear(),
                        response_movies.get(i).getOverview(), Uri.parse(Requests.getImageUrl() + response_movies.get(i).getPhoto()))));
            }
            isDoingPetition = false;
            adapter.addItems(movies);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_list_movies, container, false);

        RecyclerView recyclerView = (RecyclerView) root.findViewById(R.id.rv_movies_list);
        final EditText searchEditText = (EditText) root.findViewById(R.id.searchEditText);

        adapter = new AdapterListMovies<>(new ResourcesUtil.ImageLoader() {
            @Override
            public void loadImage(@NonNull ImageView view, @NonNull Uri uri) {
                Glide.with(view.getContext())
                        .load(uri)
                        .into(view);
            }
        });

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                querySearch = searchEditText.getText().toString();
                if(!querySearch.isEmpty()) {
                    if (!isDoingPetition) {
                        adapter.clearItems();
                        countPagination = 1;
                        isSearching = true;
                        isDoingPetition = true;
                        getHttpManager().callStart(
                                Http.RequestType.GET,
                                Requests.Values.GET_SEARCH_MOVIE,
                                "?api_key=" + getString(R.string.api_key) + "&query=" + querySearch + "&page=" + countPagination,
                                null,
                                null,
                                null,
                                true
                        );
                    }
                }else{
                    adapter.clearItems();
                    countPagination = 1;
                    isSearching = false;
                    isDoingPetition = true;
                    getHttpManager().callStart(
                            Http.RequestType.GET,
                            Requests.Values.GET_POPULAR_MOVIES,
                            "?api_key=" + getString(R.string.api_key) + "&page=" + countPagination,
                            null,
                            null,
                            null,
                            true
                    );
                }

            }
        });

        final LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int visibleItemCount = layoutManager.getChildCount();
                int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                if((visibleItemCount + firstVisibleItemPosition) >= LIMIT_MOVIES*countPagination){
                    if(!isDoingPetition) {
                        isDoingPetition = true;
                        countPagination++;
                        if(isSearching){
                            getHttpManager().callStart(
                                    Http.RequestType.GET,
                                    Requests.Values.GET_SEARCH_MOVIE,
                                    "?api_key=" + getString(R.string.api_key) + "&query=" + querySearch + "&page=" + countPagination,
                                    null,
                                    null,
                                    null,
                                    true
                            );
                        }else{
                            getHttpManager().callStart(
                                    Http.RequestType.GET,
                                    Requests.Values.GET_POPULAR_MOVIES,
                                    "?api_key=" + getString(R.string.api_key) + "&page=" + countPagination,
                                    null,
                                    null,
                                    null,
                                    true
                            );
                        }
                    }
                }
            }
        });
        recyclerView.setAdapter(adapter);

        return root;
    }
}
