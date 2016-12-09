package website.bloop.app.api;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * POJO for sending new players to Postgres.
 */

public class Player {
    @JsonProperty
    private String name;

    @JsonProperty
    private String googlePlayId;

    public Player(String name, String googlePlayId) {
        this.name = name;
        this.googlePlayId = googlePlayId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGooglePlayId() {
        return googlePlayId;
    }

    public void setGooglePlayId(String googlePlayId) {
        this.googlePlayId = googlePlayId;
    }
}
