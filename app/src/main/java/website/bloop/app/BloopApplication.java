package website.bloop.app;

import android.app.Application;

import com.google.android.gms.common.api.GoogleApiClient;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import website.bloop.app.api.APIPath;
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

    public GoogleApiClient getGoogleApiClient() {
        return mGoogleApiClient;
    }

    public void setGoogleApiClient(GoogleApiClient client) {
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
        if (mService == null) {
            mService = new Retrofit.Builder()
                    .baseUrl(APIPath.BASE_PATH)
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(JacksonConverterFactory.create())
                    .build()
                    .create(BloopAPIService.class);
        }
        return mService;
    }
}
