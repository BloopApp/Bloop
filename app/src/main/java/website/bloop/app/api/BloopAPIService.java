package website.bloop.app.api;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * Interface for sending requests to the backend.
 */

public interface BloopAPIService {
    @POST(APIPath.PLACE_FLAG)
    Call<ResponseBody> placeFlag(@Body PlacedFlag location);

    @POST(APIPath.CHECK_NEARBY)
    Call<NearbyFlag> getNearestFlag(@Body PlayerLocation location);

    @POST(APIPath.CAPTURE_FLAG)
    Call<ResponseBody> captureFlag(@Body CapturedFlag flag);

    @POST(APIPath.ADD_PLAYER)
    Call<ResponseBody> addPlayer(@Body Player player);

    @POST(APIPath.UPDATE_FIREBASE_TOKEN)
    Call<ResponseBody> updateFirebaseToken(@Body Player player);
}
