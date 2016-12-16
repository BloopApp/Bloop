package website.bloop.app;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import static website.bloop.app.BloopApplication.BLOOP_PREFERENCE_FILE;

/**
 * Firebase ID service which is used for push notifications and registering this app instance
 * with Firebase.
 */
public class BloopFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "BloopFirebaseIIDService";
    public static final String PREF_FIREBASE_TOKEN = "firebase_token";

    @Override
    public void onTokenRefresh() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        BloopApplication application = BloopApplication.getInstance();
        if (application.getPlayerId() != null) {
            application.sendFirebaseRegistrationToServer(refreshedToken);
        } else {
            storeToken(refreshedToken);
        }

    }

    private void storeToken(String token) {
        SharedPreferences sharedPrefs = getApplicationContext()
                .getSharedPreferences(BLOOP_PREFERENCE_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putString(PREF_FIREBASE_TOKEN, token);
        editor.commit();
    }
}

