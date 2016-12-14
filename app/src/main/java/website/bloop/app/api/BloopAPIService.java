package website.bloop.app.api;

import io.reactivex.Observable;
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
    Observable<ResponseBody> placeFlag(@Body PlayerLocation location);

    @POST(APIPath.CHECK_NEARBY)
    Observable<NearbyFlag> getNearestFlag(@Body PlayerLocation location);

    @POST(APIPath.CAPTURE_FLAG)
    Observable<ResponseBody> captureFlag(@Body NearbyFlag flag);

    @POST(APIPath.ADD_PLAYER)
    Observable<ResponseBody> addPlayer(@Body Player player);
}
