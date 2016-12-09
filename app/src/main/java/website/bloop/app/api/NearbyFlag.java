package website.bloop.app.api;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * POJO for receiving distance updates and capturing flags.
 */

public class NearbyFlag {
    @JsonProperty
    private double bloopFrequency;

    @JsonProperty
    private long flagId;

    @JsonProperty
    private String playerName;

    @JsonProperty
    private String capturingPlayerId;

    public NearbyFlag(long flagId, String capturingPlayerId) {
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

    public String getCapturingPlayerId() {
        return capturingPlayerId;
    }

    public void setCapturingPlayerId(String capturingPlayerId) {
        this.capturingPlayerId = capturingPlayerId;
    }
}
