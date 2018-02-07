package querol.pol.tmdbapp.http;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.gson.Gson;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by Pol Querol on 5/2/18.
 */

public class Http {
    private static final String TAG = Http.class.getSimpleName();
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static Http ourInstance = new Http();
    public static Http getInstance() {
        return ourInstance;
    }

    public static final String REQUEST = "HTTP_request";
    public static final String ERROR = "HTTP_error";
    public static final String RESPONSE = "HTTP_response";

    private OkHttpClient httpClient;
    private ExecutorService exService;
    private Context appContext;

    private Http() {
        exService = Executors.newCachedThreadPool();
    }

    public enum RequestType {
        GET, POST, PUT, DELETE
    }

    public void initClient() {
        if (httpClient == null) {
            httpClient = new OkHttpClient();
        } else {
            Log.w(TAG, "Http client only needs to be initialized once!");
        }
    }

    public void initContext(Context context) {
        if(appContext == null) {
            appContext = context.getApplicationContext();
        } else {
            Log.w(TAG, "Application context only needs to be set once!");
        }
    }

    public static Request createRequest(
            RequestType type, String url, @Nullable Object obj, @Nullable Map<String, String>  headers
    ) {
        Request.Builder builder = new Request.Builder()
                .url(url);

        if(headers != null) {
            for(Map.Entry<String, String> entry: headers.entrySet()){
                builder.addHeader(entry.getKey(), entry.getValue());
            }
        }

        String json = "";
        switch (type) {
            case POST:
            case PUT:
                json = new Gson().toJson(obj);
                break;
        }

        switch (type) {
            case GET:
                Log.v(TAG, REQUEST + "(GET): " + url);
                builder.get();
                break;
            case POST:
                Log.v(TAG, REQUEST + "(POST): " + url + "\nJSON" + json);
                builder.post(RequestBody.create(JSON, json));
                break;
            case PUT:
                Log.v(TAG, REQUEST + "(PUT): " + url + "\nJSON" + json);
                builder.put(RequestBody.create(JSON, json));
                break;
            case DELETE:
                Log.v(TAG, REQUEST + "(PUT): " + url + "\nJSON" + json);
                builder.delete(RequestBody.create(JSON, json));
                break;
        }

        return builder.build();
    }

    public Future call(Request request, String requestIdentifier) {
        return call(request, requestIdentifier, HttpRunnable.DEFAULT_MAX_ATTEMPTS);
    }

    public Future call(Request request, String requestIdentifier, int maxAttempts) {
        if(httpClient != null) {
            if(appContext != null) {
                return exService.submit(
                        new HttpRunnable(
                                httpClient, appContext, request, requestIdentifier,
                                HttpRunnable.DEFAULT_SLEEP_TIME, maxAttempts
                        )
                );
            } else {
                throw new NullPointerException("Application context not defined");
            }
        } else {
            throw new NullPointerException("HttpClient not initialized");
        }
    }
}
