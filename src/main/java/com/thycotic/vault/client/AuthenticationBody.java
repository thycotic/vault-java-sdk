package com.thycotic.vault.client;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Representation of the request for authenticating within the service. This implementation requires client credentials.
 * This could be abstracted in the future.
 *
 * @author dsv@thycotic.com
 * @since 1.0.0
 */
public class AuthenticationBody {
    @JsonProperty("grant_type")
    private String grantType;

    @JsonProperty("client_id")
    private String clientId;

    @JsonProperty("client_secret")
    private String clientSecret;

    public AuthenticationBody(String clientId, String clientSecret) {
        this.grantType = "client_credentials";
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    public String getClientId() {
        return clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }
}
