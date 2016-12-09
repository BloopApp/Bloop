package website.bloop.app.api;

/**
 * URLs for exchanging data with the backend API
 */

public interface APIPath {
    String BASE_PATH = "http://10.26.68.132:8081/api/"; //TODO: change this to something that isn't an internal IP
    String PLACE_FLAG = "flag/place";
    String CHECK_NEARBY = "flag/nearby";
    String CAPTURE_FLAG = "flag/capture";
    String ADD_PLAYER = "players/new";
}
