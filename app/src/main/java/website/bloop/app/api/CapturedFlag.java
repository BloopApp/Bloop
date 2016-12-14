package website.bloop.app.api;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * POJO for capturing flags.
 */

public class CapturedFlag {
    @JsonProperty
    private long flagId;

    @JsonProperty
    private String capturingPlayerId;

    public CapturedFlag(long flagId, String capturingPlayerId) {
        this.flagId = flagId;
        this.capturingPlayerId = capturingPlayerId;
    }

    public CapturedFlag() { }

    public long getFlagId() {
        return flagId;
    }

    public void setFlagId(long flagId) {
        this.flagId = flagId;
    }

    public String getCapturingPlayerId() {
        return capturingPlayerId;
    }

    public void setCapturingPlayerId(String capturingPlayerId) {
        this.capturingPlayerId = capturingPlayerId;
    }
}
