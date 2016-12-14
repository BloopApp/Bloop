package website.bloop.app;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
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

        Call<ResponseBody> call = application.getService().updateFirebaseToken(player);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }
}
