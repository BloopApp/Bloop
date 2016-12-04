package website.bloop.app.api;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * TODO: Add a class header comment!
 */

public class NearbyFlag {
    @JsonProperty
    private double bloopFrequency;

    @JsonProperty
    private long flagId;

    @JsonProperty
    private String playerName;

    @JsonProperty
    private long capturingPlayerId;

    public NearbyFlag(long flagId, long capturingPlayerId) {
        this.flagId = flagId;
        this.capturingPlayerId = capturingPlayerId;
    }

    public NearbyFlag() { }

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

    public long getCapturingPlayerId() {
        return capturingPlayerId;
    }

    public void setCapturingPlayerId(long capturingPlayerId) {
        this.capturingPlayerId = capturingPlayerId;
    }
}
