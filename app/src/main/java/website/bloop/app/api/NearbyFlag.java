package website.bloop.app.api;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * POJO for receiving distance updates.
 */

public class NearbyFlag {
    @JsonProperty
    private double bloopFrequency;

    @JsonProperty
    private long flagId;

    @JsonProperty
    private String playerName;

    @JsonProperty
    private int color;

    public NearbyFlag() {
    }

    public double getBloopFrequency() {
        return bloopFrequency;
    }

    public void setBloopFrequency(double bloopFrequency) {
        this.bloopFrequency = bloopFrequency;
    }

    public long getFlagId() {
        return flagId;
    }

    public void setFlagId(long flagId) {
        this.flagId = flagId;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}
