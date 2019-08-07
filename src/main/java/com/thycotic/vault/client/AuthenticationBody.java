package com.thycotic.vault.client;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Representation of the request for authenticating within the service.
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

    @JsonProperty("aws_headers")
    private String awsHeaders;

    @JsonProperty("aws_body")
    private String awsBody;

    public AuthenticationBody(String clientId, String clientSecret) {
        this.grantType = "client_credentials";
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    public AuthenticationBody(String grantType) {
        this.grantType = grantType;
    }

    public String getClientId() {
        return clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public String getAwsHeaders() { return awsHeaders; }

    public String getAwsBody() { return awsBody; }

    public void setAwsHeaders(String awsHeaders) {
        this.awsHeaders = awsHeaders;
    }

    public void setAwsBody(String awsBody) {
        this.awsBody = awsBody;
    }
}
