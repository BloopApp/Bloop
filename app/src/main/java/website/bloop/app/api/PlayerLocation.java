package website.bloop.app.api;

import android.location.Location;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * POJO for sending player location to the server.
 */

public class PlayerLocation {
    @JsonProperty
    private String googlePlayId;

    @JsonProperty
    private double latitude;

    @JsonProperty
    private double longitude;

    public PlayerLocation(String googlePlayId, double latitude, double longitude) {
        this.googlePlayId = googlePlayId;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public PlayerLocation(String googlePlayId, Location location) {
        this.googlePlayId = googlePlayId;
        this.latitude = location.getLatitude();
        this.longitude = location.getLongitude();
    }

    public PlayerLocation() {
    }

    public String getGooglePlayId() {
        return googlePlayId;
    }

    public void setGooglePlayId(String googlePlayId) {
        this.googlePlayId = googlePlayId;
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
