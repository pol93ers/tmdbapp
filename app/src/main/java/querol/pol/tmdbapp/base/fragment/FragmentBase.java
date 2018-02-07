package querol.pol.tmdbapp.base.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;

import java.util.ArrayList;
import java.util.List;

import querol.pol.tmdbapp.base.activity.ActivityBase;
import querol.pol.tmdbapp.http.HttpBroadcastManager;
import querol.pol.tmdbapp.http.project.Requests;
import querol.pol.tmdbapp.http.project.response.Response;
import querol.pol.tmdbapp.http.project.response.ResponseError;
import querol.pol.tmdbapp.util.Component;
import querol.pol.tmdbapp.util.FragmentHelper;
import querol.pol.tmdbapp.util.LogUtils;

/**
 * <p>Base class for all Fragments. Extends support.v4.app.Fragment</p>
 * <p>Created by Eduardo Ferreras on 09/11/2016.</p>
 */
public abstract class FragmentBase extends Fragment implements Component, HttpBroadcastManager.HttpBroadcastListener {
    protected final String TAG = getComponent().id();
    private FragmentHelper fragmentHelper;

    private HttpBroadcastManager httpManager;
    private List<AlertDialog> dismissibleDialogs;

    public FragmentBase() {
        dismissibleDialogs = new ArrayList<>();
    }

    private OnFragmentListener listener;
    public interface OnFragmentListener {
        void onHttpShowProgressDialog(Requests.Values val, boolean showLoading);
        void onHttpCancelProgressDialog(Requests.Values val, boolean showLoading);
        AlertDialog onShowAlertDialog1bt(
                String title, String message,
                String positiveString, DialogInterface.OnClickListener positiveListener
        );
        AlertDialog onShowAlertDialog2bt(
                String title, String message,
                String positiveString, DialogInterface.OnClickListener positiveListener,
                String negativeString, DialogInterface.OnClickListener negativeListener
        );
        void onAddDismissibleDialog(AlertDialog aDialog, List<AlertDialog> dismissibleDialogs);
        void onDismissDialogs(List<AlertDialog> dismissibleDialogs);

        <F extends Fragment & Component> void onFinish(F fragment);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        listener = onAttachGetListener(OnFragmentListener.class, context);
        httpManager = new HttpBroadcastManager(this);
    }

    @CallSuper @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragmentHelper = new FragmentHelper(getChildFragmentManager());
        LogUtils.v(TAG, TAG + " created!");
    }

    @CallSuper @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtils.v(TAG, TAG + " destroyed!");
    }


    protected FragmentHelper getFragmentHelper() {
        return fragmentHelper;
    }

    protected <T> T onAttachGetListener(Class<T> tClass, Context parent) {
        try { return tClass.cast(parent); }
        catch (ClassCastException e) {
            throw new ClassCastException(String.valueOf(parent)
                    + " must implement " + tClass.getSimpleName());
        }
    }

    //region HttpCall
    //----------------------------------------------------------------------------------------------

    public HttpBroadcastManager getHttpManager() {
        return httpManager;
    }

    @Override
    public void onHttpCallStart(String requestId, boolean showLoading) {
        ((ActivityBase)getActivity()).onHttpCallStart(requestId, showLoading);
    }

    @Override
    public void onHttpBroadcastError(String requestId, ResponseError response) {
        ((ActivityBase)getActivity()).onHttpBroadcastError(requestId, response);
    }

    @Override
    public void onHttpBroadcastSuccess(String requestId, Response response) {
        ((ActivityBase)getActivity()).onHttpBroadcastSuccess(requestId, response);
    }

    @Override
    public void onHttpCallEnd(String requestId, boolean showLoading) {
        ((ActivityBase)getActivity()).onHttpCallEnd(requestId, showLoading);
    }

    @Override
    public boolean onCanExecuteBroadcastResponse(String requestId) {
        return isResumed();
    }

    //----------------------------------------------------------------------------------------------
    //endregion HttpCall

    //region Dialogs
    //----------------------------------------------------------------------------------------------

    protected void showHttpProgressDialog(Requests.Values val, boolean showLoading) {
        listener.onHttpShowProgressDialog(val, showLoading);
    }

    protected void cancelHttpProgressDialog(Requests.Values val, boolean showLoading) {
        listener.onHttpCancelProgressDialog(val, showLoading);
    }

    protected AlertDialog showAlertDialog1bt(
            String title, String message,
            String positiveString, DialogInterface.OnClickListener positiveListener
    ) {
        return listener.onShowAlertDialog1bt(title, message, positiveString, positiveListener);
    }

    protected AlertDialog showAlertDialog2bt(
            String title, String message,
            String positiveString, DialogInterface.OnClickListener positiveListener,
            String negativeString, DialogInterface.OnClickListener negativeListener
    ) {
        return listener.onShowAlertDialog2bt(
                title, message,
                positiveString, positiveListener,
                negativeString, negativeListener
        );
    }

    protected void addDismissibleDialog(AlertDialog aDialog) {
        listener.onAddDismissibleDialog(aDialog, dismissibleDialogs);
    }

    protected void dismissDialogs() {
        listener.onDismissDialogs(dismissibleDialogs);
    }

    //----------------------------------------------------------------------------------------------
    //endregion Dialogs
}
