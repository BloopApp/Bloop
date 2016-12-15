package website.bloop.app.api;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * TODO: Add a class header comment!
 */

public class OwnFlag {
    @JsonProperty
    private Boolean doesExist;

    @JsonProperty
    private Long flagId;

    @JsonProperty
    private Double latitude;

    @JsonProperty
    private Double longitude;

    public OwnFlag(Boolean doesExist, Long flagId, Double latitude, Double longitude) {
        super();
        this.doesExist = doesExist;
        this.flagId = flagId;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public OwnFlag() { }

    public Boolean getDoesExist() {
        return doesExist;
    }

    public void setDoesExist(Boolean doesExist) {
        this.doesExist = doesExist;
    }

    public Long getFlagId() {
        return flagId;
    }

    public void setFlagId(Long flagId) {
        this.flagId = flagId;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
}
