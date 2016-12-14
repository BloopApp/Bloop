package website.bloop.app.api;

/**
 * URLs for exchanging data with the backend API
 */

public interface APIPath {
    String BASE_PATH = "https://bloop-api.wolfd.io/api/"; //TODO: change this to something that isn't an internal IP
    String PLACE_FLAG = "flag/place";
    String CHECK_NEARBY = "flag/nearby";
    String CAPTURE_FLAG = "flag/capture";
    String ADD_PLAYER = "players/new";
    String UPDATE_FIREBASE_TOKEN = "players/update-firebase";
}
