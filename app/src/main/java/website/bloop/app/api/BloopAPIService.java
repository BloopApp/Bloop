package website.bloop.app.api;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Interface for sending requests to the backend.
 */

public interface BloopAPIService {
    @POST(APIPath.PLACE_FLAG)
    Observable<ResponseBody> placeFlag(@Body PlacedFlag flag);

    @POST(APIPath.CHECK_NEARBY)
    Observable<NearbyFlag> getNearestFlag(@Body PlayerLocation location);

    @POST(APIPath.CAPTURE_FLAG)
    Observable<ResponseBody> captureFlag(@Body CapturedFlag flag);

    @POST(APIPath.ADD_PLAYER)
    Observable<ResponseBody> addPlayer(@Body Player player);

    @POST(APIPath.UPDATE_FIREBASE_TOKEN)
    Observable<ResponseBody> updateFirebaseToken(@Body Player player);

    @GET(APIPath.HAS_PLACED_FLAG)
    Observable<OwnFlag> checkHasPlacedFlag(@Query("id") String googlePlayId);

    @DELETE(APIPath.DELETE_FLAG)
    Observable<OwnFlag> deleteFlag(@Query("id") String googlePlayId);
}
