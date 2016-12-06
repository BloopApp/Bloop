package website.bloop.app;

import android.app.Application;

import com.google.android.gms.common.api.GoogleApiClient;

/**
 *
 */
public class BloopApplication extends Application {
    private static BloopApplication mInstance = null;

    private GoogleApiClient mGoogleApiClient;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }

    public static BloopApplication getInstance(){
        if(mInstance == null)
        {
            mInstance = new BloopApplication();
        }
        return mInstance;
    }

    public void setClient(GoogleApiClient client) {
        mGoogleApiClient = client;
    }

    public GoogleApiClient getClient() {
        return mGoogleApiClient;
    }
}
