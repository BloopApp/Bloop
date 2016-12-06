package website.bloop.app.api;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * POJO for sending player location to the server.
 */

public class PlayerLocation {
    @JsonProperty
    private long playerId;

    @JsonProperty
    private double latitude;

    @JsonProperty
    private double longitude;

    public PlayerLocation(long playerId, double latitude, double longitude) {
        this.playerId = playerId;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public PlayerLocation() { }

    public long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(long playerId) {
        this.playerId = playerId;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
