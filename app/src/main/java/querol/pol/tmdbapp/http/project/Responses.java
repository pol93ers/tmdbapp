package querol.pol.tmdbapp.http.project;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import querol.pol.tmdbapp.http.Http;
import querol.pol.tmdbapp.http.project.response.Response;
import querol.pol.tmdbapp.http.project.response.ResponseError;
import querol.pol.tmdbapp.http.project.response.ResponseListMovies;

/**
 * Created by Pol Querol on 5/2/18.
 */

public class Responses {

    private static final String TAG = Responses.class.getSimpleName();

    private static Responses ourInstance = new Responses();
    public static Responses getInstance() {return ourInstance;}

    private Gson gson;

    private Responses() {gson = new Gson();}

    public Object handleResponse(
            okhttp3.Response httpResponse, String requestId
    ) throws InstantiationException, IllegalAccessException {
        if (requestId.equals(Http.ERROR)) {
            return getFromBodyJson(httpResponse, ResponseError.class, requestId);
        } else if (requestId.equals(Requests.Values.GET_POPULAR_MOVIES.id)){
            return getFromBodyJson(httpResponse, ResponseListMovies.class, requestId);
        } else if (requestId.equals(Requests.Values.GET_SEARCH_MOVIE.id)){
            return getFromBodyJson(httpResponse, ResponseListMovies.class, requestId);
        } else {
            httpResponse.body().close();
            throw new IllegalArgumentException("Response for " + requestId + " not implemented!");
        }
    }


    private <T extends Response> Response getFromBodyJson(
            okhttp3.Response httpResponse, Class<T> cls, String requestId
    ) throws IllegalAccessException, InstantiationException {
        Response obj = null;
        try {
            obj = gson.fromJson(httpResponse.body().charStream(), cls);
        } catch (JsonIOException | JsonSyntaxException e) {
            Log.w(TAG, "Error parsing object of type " + cls.getSimpleName(), e);
            httpResponse.body().close();
        }
        if (obj == null) obj = cls.newInstance();
        obj.setHttpStatus(httpResponse);
        obj.setRequestId(requestId);
        return obj;
    }
}
