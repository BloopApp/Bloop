package website.bloop.app;

import android.app.Application;

import com.google.android.gms.common.api.GoogleApiClient;

import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import website.bloop.app.api.APIPath;
import website.bloop.app.api.BloopAPIService;

/**
 *
 */
public class BloopApplication extends Application {
    private static BloopApplication mInstance = null;

    private GoogleApiClient mGoogleApiClient;
    private String playerId;
    private String playerName;

    private BloopAPIService mService;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;

        mService = new Retrofit.Builder()
                .baseUrl(APIPath.BASE_PATH)
                .addConverterFactory(JacksonConverterFactory.create())
                .build()
                .create(BloopAPIService.class);
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
}
