package querol.pol.tmdbapp.http;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.io.IOException;
import java.io.Serializable;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import querol.pol.tmdbapp.http.project.Responses;

/**
 * Created by Pol Querol on 5/2/18.
 */

public class HttpRunnable extends Thread {
    private static final String TAG = HttpRunnable.class.getSimpleName();

    private OkHttpClient httpClient;
    private Context appContext;
    private Request request;
    private String requestIdentifier;

    public static final int DEFAULT_SLEEP_TIME = 3000;
    public static final int DEFAULT_MAX_ATTEMPTS = 4;
    private int sleepTime;
    private int maxAttempts;
    private int currentAttempts;

    private Response response = null;

    public HttpRunnable(
            OkHttpClient httpClient, Context appContext,
            Request request, String requestIdentifier,
            int sleepTime, int maxAttempts
    ) {
        this.httpClient = httpClient;
        this.appContext = appContext;
        this.request = request;
        this.requestIdentifier = requestIdentifier;
        this.sleepTime = sleepTime;
        this.maxAttempts = maxAttempts;
        this.currentAttempts = 0;
    }

    @Override
    public void run() {
        try {
            // Try to get a valid response until forced to stop
            do {
                try {
                    closeResponseBody(response);

                    // Wait until trying again.
                    if (currentAttempts > 0 && sleepTime > 0) {
                        sleep(currentAttempts * sleepTime);
                    }
                    // Attempt to get response
                    currentAttempts += 1;
                    call(request);

                } catch (IOException e) {
                    Log.w(TAG, Http.ERROR + "(" + currentAttempts + "): " + requestIdentifier
                            + "\n" + e.getMessage());
                }

                if (response != null && !isValidCode(response.code())) { break; }
            } while (canRetry() && response == null);

            try {
                // Send response
                if (response != null) {
                    if (response.isSuccessful()) {
                        sendOkMessage(
                                responseToObject(response, requestIdentifier)
                        );
                    } else {
                        sendErrorMessage(
                                responseToObject(response, Http.ERROR)
                        );
                    }
                } else {
                    sendErrorMessage(
                            null
                    );
                }
                closeResponseBody(response);
            } catch (Exception e) {
                // Should never happen
                closeResponseBody(response);
                Log.wtf(TAG, "Thread for request \"" + requestIdentifier + "\" had some weird stuff:", e);
                sendErrorMessage(
                        null
                );
            }
        } catch (InterruptedException e) {
            closeResponseBody(response);
            Log.v(TAG, "Thread for request \""  + requestIdentifier + "\" interrupted while sleeping:"
                    + "\n" + e.getMessage());
        } catch (Exception e) {
            closeResponseBody(response);
            Log.e(TAG, "Thread for request \"" + requestIdentifier + "\" ended because of unexpected error:", e);
            sendErrorMessage(
                    null
            );
        }
    }

    private void call (Request request)
            throws IOException {
        Call call = httpClient.newCall(request);
        response = call.execute();

        Log.v(TAG, response.toString());
        if (!response.isSuccessful()) {
            throw new IOException("Unexpected code " + response);
        }
    }


    /**
     * Checks if runnable has more attempts available or it has used all
     * @return true if it can, false otherwise
     */
    private boolean canRetry() {
        if (maxAttempts < 1 || currentAttempts < maxAttempts) {
            return true;
        } else {
            Log.v(TAG, "Thread for request \"" + requestIdentifier + "\" has run out of attempts");
            return false;
        }
    }

    /**
     * Checks if code is a successful http code (Except for 401)
     * @param code A Http code
     * @return true if it is, false otherwise
     */
    private boolean isValidCode(int code) {
        if (!(code >= 400 && code != 401)) {
            return true;
        } else {
            Log.e(TAG, "Thread for request \"" + requestIdentifier + "\" received unrecoverable http code: " + code);
            return false;
        }
    }



    private void closeResponseBody(Response response) {
        if (response != null) {
            response.body().close();
        }
    }

    private Object responseToObject(Response response, String responseId)
            throws IllegalAccessException, InstantiationException
    {
        return Responses.getInstance().handleResponse(response, responseId);
    }



    private void sendOkMessage(Object obj) {
        Intent intent = new Intent(requestIdentifier);
        intent.putExtra(Http.ERROR, false);

        Log.d(TAG, Http.RESPONSE + "(" + currentAttempts + "): " + requestIdentifier
                + "\n" + obj);

        intent.putExtra(Http.RESPONSE, (Serializable)obj);
        LocalBroadcastManager.getInstance(appContext).sendBroadcast(intent);
    }

    private void sendErrorMessage(Object obj) {
        Intent intent = new Intent(requestIdentifier);
        intent.putExtra(Http.ERROR, true);

        Log.d(TAG, Http.RESPONSE + "(" + currentAttempts + "): " + requestIdentifier
                + "\n" + obj);

        if (obj != null) { intent.putExtra(Http.RESPONSE, (Serializable)obj); }
        LocalBroadcastManager.getInstance(appContext).sendBroadcast(intent);
    }
}
