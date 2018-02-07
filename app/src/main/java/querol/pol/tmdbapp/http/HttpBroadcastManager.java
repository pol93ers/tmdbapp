package querol.pol.tmdbapp.http;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.Future;

import okhttp3.Request;
import querol.pol.tmdbapp.http.project.Requests;
import querol.pol.tmdbapp.http.project.response.Response;
import querol.pol.tmdbapp.http.project.response.ResponseError;

/**
 * Created by Pol Querol on 5/2/18.
 */

public class HttpBroadcastManager {
    private static final String TAG = "HttpBroadcastManager";

    private final HashMap<String, Future> calls;
    private final BroadcastReceiver receiver;
    private final ArrayList<Response> pendingResponses;
    private final HttpBroadcastListener listener;
    private static boolean showLoading;

    public HttpBroadcastManager(HttpBroadcastListener listener) {
        this.calls = new HashMap<>();
        this.listener = listener;
        this.pendingResponses = new ArrayList<>();
        this.receiver = createReceiver(this.calls, this.listener, this.pendingResponses);
    }

    public interface HttpBroadcastListener {
        void onHttpCallStart(String requestId, boolean showLoading);
        void onHttpBroadcastError(String requestId, ResponseError response);
        void onHttpBroadcastSuccess(String requestId, Response response);
        void onHttpCallEnd(String requestId, boolean showLoading);
        boolean onCanExecuteBroadcastResponse(String requestId);
    }

    private static BroadcastReceiver createReceiver(
            final HashMap<String, Future> calls,
            final HttpBroadcastListener listener,
            final ArrayList<Response> pendingResponses
    ) {
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String act = intent.getAction();
                for (Map.Entry<String, Future> entry: calls.entrySet()) {
                    String requestId = entry.getKey();
                    if(requestId.equals(act)) {
                        if (intent.getBooleanExtra(Http.ERROR, true)) {
                            ResponseError response;
                            if(intent.getSerializableExtra(Http.RESPONSE) != null) {
                                response = (ResponseError)intent.getSerializableExtra(Http.RESPONSE);
                            } else {
                                response = null;
                            }
                            if (listener.onCanExecuteBroadcastResponse(requestId)) {
                                Log.d(TAG, "Executing error response: " + requestId);
                                calls.put(requestId, null);
                                listener.onHttpBroadcastError(requestId, response);
                                listener.onHttpCallEnd(requestId, showLoading);
                            } else {
                                Log.d(TAG, "Saving pending error response: " + requestId);
                                pendingResponses.add(response);
                            }
                        } else {
                            Response response;
                            if(intent.getSerializableExtra(Http.RESPONSE) != null) {
                                response = (Response)intent.getSerializableExtra(Http.RESPONSE);
                            } else {
                                response = null;
                            }
                            if (listener.onCanExecuteBroadcastResponse(requestId)) {
                                Log.d(TAG, "Executing response: " + requestId);
                                calls.put(requestId, null);
                                listener.onHttpBroadcastSuccess(requestId, response);
                                listener.onHttpCallEnd(requestId, showLoading);
                            } else {
                                Log.d(TAG, "Saving pending response: " + requestId);
                                pendingResponses.add(response);
                            }
                        }
                        break;
                    }
                }
            }
        };
    }

    public int getSize() {
        int size = 0;
        for (Map.Entry<String, Future> entry : calls.entrySet())
        {
            if (entry.getValue() != null) { size += 1; }
        }
        return size;
    }

    public void receiverRegister(Context context, Requests.Values request) {
        calls.put(request.id, null);
        LocalBroadcastManager.getInstance(context).registerReceiver(
                receiver, new IntentFilter(request.id)
        );
    }

    public void receiverUnregister(Context context) {
        calls.clear();
        LocalBroadcastManager.getInstance(context).unregisterReceiver(receiver);
    }

    public boolean isCallRegistered(Requests.Values val) {
        return calls.containsKey(val.id);
    }

    public boolean isCallRunning(Requests.Values val) {
        return calls.get(val.id) != null && !calls.get(val.id).isDone();
    }

    public void callStart(
            Http.RequestType type, Requests.Values request, String url,
            @Nullable Request requestObj, @Nullable String token, @Nullable Integer numAttempts, boolean showLoading
    ) {
        if (isCallRegistered(request)) {
            HttpBroadcastManager.showLoading = showLoading;

            Log.d(TAG, "Starting call to " + request.id);
            if (isCallRunning(request)) {
                Log.w(TAG, "Call to " + request.id + " is already running, so it will be saved over.");
            }

            // Api Key
            Map<String, String> headers;
            if (token != null) {
                headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + token);
            } else {
                headers = null;
            }

            // Create request
            if(url == null) url = "";
            okhttp3.Request requestToSend
                    = Http.createRequest(type, request.where + url, requestObj, headers);

            listener.onHttpCallStart(request.id, showLoading);
            // Call request
            Future call;
            if (numAttempts != null) {
                call = Http.getInstance().call(requestToSend, request.id, numAttempts);
            } else  {
                call = Http.getInstance().call(requestToSend, request.id);
            }

            // Save call
            calls.put(request.id, call);
            ignorePendingResponses(new Requests.Values[]{request});
        } else {
            throw new IllegalArgumentException("Attempted to start an unregistered call to " + request.id);
        }
    }

    public boolean callCancel(Requests.Values request, boolean showLoading) {
        if (calls.containsKey(request.id)) {
            if (calls.get(request.id) != null && !calls.get(request.id).isDone()) {
                calls.get(request.id).cancel(true);
            }
            calls.put(request.id, null);
            listener.onHttpCallEnd(request.id, showLoading);
            return true;
        }
        return false;
    }



    private boolean executeResponse(String requestId, Response response) {
        if (requestId.equals(response.getRequestId())) {
            if (response instanceof ResponseError) {
                Log.d(TAG, "Executing pending error response: " + requestId);
                listener.onHttpBroadcastError(requestId, (ResponseError)response);
            } else {
                Log.d(TAG, "Executing pending response: " + requestId);
                listener.onHttpBroadcastSuccess(requestId, response);
            }
            calls.put(requestId, null);
            listener.onHttpCallEnd(requestId, showLoading);
            return true;
        } else {
            return false;
        }
    }

    public void executePendingResponses(@Nullable Requests.Values[] values) {
        if (values != null) {
            for (Requests.Values value : values) {
                Iterator<Response> i = pendingResponses.iterator();
                while (i.hasNext()) {
                    Response response = i.next();
                    if (executeResponse(value.id, response)) {
                        i.remove();
                    }
                }
            }
        } else {
            Iterator<Response> i = pendingResponses.iterator();
            while (i.hasNext()) {
                Response response = i.next();
                if (executeResponse(response.getRequestId(), response)) {
                    i.remove();
                }
            }
        }
    }

    public void ignorePendingResponses(@Nullable Requests.Values[] values) {
        if (values != null) {
            for (Requests.Values value : values) {
                Iterator<Response> i = pendingResponses.iterator();
                while (i.hasNext()) {
                    Response response = i.next();
                    if (response.getRequestId().equals(value.id)) {
                        i.remove();
                    }
                }
            }
        } else {
            Iterator<Response> i = pendingResponses.iterator();
            while (i.hasNext()) {
                i.next();
                i.remove();
            }
        }
    }

    public boolean hasPendingResponse(Requests.Values request) {
        for (Response response: pendingResponses) {
            if (response != null && response.getRequestId().equals(request.id)) {
                return true;
            }
        }
        return false;
    }
}
