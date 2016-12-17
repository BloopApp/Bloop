package website.bloop.app;

import android.content.Intent;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import website.bloop.app.activities.BloopActivity;

import static android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP;
import static website.bloop.app.activities.BloopActivity.ARG_OPPONENT;

/**
 * Push notification logic with Firebase.
 */
public class BloopFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "BloopFirebaseMsgService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage.getNotification() != null) {
            Log.i(TAG, "Message data: " + remoteMessage.getData());
            Log.i(TAG, "Message body: " + remoteMessage.getNotification().getBody());


            Intent intent = new Intent(this, BloopActivity.class);

            intent.addFlags(FLAG_ACTIVITY_SINGLE_TOP);
            intent.putExtra(ARG_OPPONENT, remoteMessage.getNotification().getBody());

            startActivity(intent);
        }
    }
}
