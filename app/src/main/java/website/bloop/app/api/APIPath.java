package website.bloop.app.api;

/**
 * URLs for exchanging data with the backend API
 */

public interface APIPath {
    String BASE_PATH = "http://bloop-api.wolfd.io:8081/api/"; //TODO: change this to something that isn't an internal IP
    String PLACE_FLAG = "flag/place";
    String CHECK_NEARBY = "flag/nearby";
    String CAPTURE_FLAG = "flag/capture";
    String ADD_PLAYER = "players/new";
}
