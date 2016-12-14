package website.bloop.app;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import website.bloop.app.api.Player;

public class BloopFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "BloopFirebaseIIDService";

    @Override
    public void onTokenRefresh() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        sendRegistrationToServer(refreshedToken);
    }

    private void sendRegistrationToServer(String token) {
        BloopApplication application = BloopApplication.getInstance();
        Player player = new Player(null, application.getPlayerId(), token);

        application.getService().updateFirebaseToken(player)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        responseBody -> {},
                        throwable -> Log.e(TAG, throwable.getMessage())
                );
    }
}
