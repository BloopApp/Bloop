package website.bloop.app;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.iid.FirebaseInstanceId;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import website.bloop.app.api.APIPath;
import website.bloop.app.api.BloopAPIService;
import website.bloop.app.api.Player;

/**
 *
 */
public class BloopApplication extends Application {
    private static BloopApplication mInstance = null;
    private static final String TAG = "BloopApplicationClass";
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

    public void sendFirebaseRegistrationToServer(String token) {
        Player player = new Player(null, getPlayerId(), token);

        getService().updateFirebaseToken(player)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        responseBody -> {},
                        throwable -> Log.e(TAG, throwable.getMessage())
                );
    }
}
