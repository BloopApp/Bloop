package website.bloop.app;

import android.app.Application;

import com.google.android.gms.common.api.GoogleApiClient;

import website.bloop.app.api.BloopAPIService;

/**
 *
 */
public class BloopApplication extends Application {
    private static BloopApplication mInstance = null;
    public static final String BLOOP_PREFERENCE_FILE = "BloopPrefs";

    private GoogleApiClient mGoogleApiClient;
    private BloopAPIService mService;

    private String playerId;
    private String playerName;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }

    public static BloopApplication getInstance() {
        if (mInstance == null) {
            mInstance = new BloopApplication();
        }

        return mInstance;
    }

    public GoogleApiClient getClient() {
        return mGoogleApiClient;
    }

    public void setClient(GoogleApiClient client) {
        mGoogleApiClient = client;
    }

    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public BloopAPIService getService() {
        return mService;
    }

    public void setService(BloopAPIService service) {
        mService = service;
    }
}
