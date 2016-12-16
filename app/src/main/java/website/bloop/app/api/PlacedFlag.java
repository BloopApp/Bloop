package website.bloop.app.api;

import android.location.Location;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * POJO for placing flags.
 */
public class PlacedFlag {
    @JsonProperty
    private String googlePlayId;

    @JsonProperty
    private double latitude;

    @JsonProperty
    private double longitude;

    @JsonProperty
    private int color;

    public PlacedFlag(String googlePlayId, Location location, int color) {
        this.googlePlayId = googlePlayId;
        this.latitude = location.getLatitude();
        this.longitude = location.getLongitude();
        this.color = color;
    }

    public PlacedFlag() {
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

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    @Override
    public String toString() {
        return "PlacedFlag: " + getGooglePlayId() + " placed a flag with color " + getColor() + " at " + getLatitude() + ", " + getLongitude();
    }
}
