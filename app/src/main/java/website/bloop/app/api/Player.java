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

    @JsonProperty
    private String firebaseToken;

    public Player(String name, String googlePlayId, String firebaseToken) {
        this.name = name;
        this.googlePlayId = googlePlayId;
        this.firebaseToken = firebaseToken;
    }

    public Player() {
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

    public String getFirebaseToken() {
        return firebaseToken;
    }

    public void setFirebaseToken(String firebaseToken) {
        this.firebaseToken = firebaseToken;
    }
}
