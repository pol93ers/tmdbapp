package querol.pol.tmdbapp.base.app;

import android.app.Application;

import querol.pol.tmdbapp.util.Component;

/**
 * Created by Pol Querol on 5/2/18.
 */

public abstract class ApplicationBase extends Application implements Component{
    protected final String TAG = getComponent().id();

    @Override
    public void onCreate() {
        super.onCreate();

    }
}
