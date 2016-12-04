package website.bloop.app.api;

import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * TODO: Add a class header comment!
 */

public interface BloopAPIService {
    @POST(APIPath.PLACE_FLAG)
    Call<ResponseBody> placeFlag(@Body PlayerLocation location);

    @GET(APIPath.GET_NEARBY)
    Call<NearbyFlag> getNearestFlag();

    @POST(APIPath.CAPTURE_FLAG)
    Call<ResponseBody> captureFlag(@Body NearbyFlag flag);
}
