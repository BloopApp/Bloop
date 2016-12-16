package website.bloop.app;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * TODO: Add a class header comment!
 */

/**
 * Push notification logic with Firebase.
 */
public class BloopFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "BloopFirebaseMsgService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage.getData().size() > 0) {
            Log.i(TAG, "Message data: " + remoteMessage.getData());
        }
    }
}
