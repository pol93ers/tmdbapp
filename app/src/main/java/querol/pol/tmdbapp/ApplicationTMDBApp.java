package querol.pol.tmdbapp;

import android.support.annotation.NonNull;

import querol.pol.tmdbapp.base.app.ApplicationBase;
import querol.pol.tmdbapp.http.Http;

/**
 * Created by Pol Querol on 5/2/18.
 */

public class ApplicationTMDBApp extends ApplicationBase{

    @NonNull
    @Override
    public ID getComponent() {
        return ID.Application;
    }

    private static ApplicationTMDBApp instance;
    public static ApplicationTMDBApp getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;

        Http.getInstance().initClient();
        Http.getInstance().initContext(this);
    }
}