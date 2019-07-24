package com.thycotic.vault.client;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thycotic.vault.exceptions.DevOpsSecretsVaultException;
import com.thycotic.vault.utils.HTTPCodeAnalyzer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Basic client implementation to retrieve credentials using auth endpoint
 */
public class BaseClient implements IDevOpsSecretsVaultClient {
    private String tenant, clientId, clientSecret, vaultUrl;
    private final String TOKEN_ENDPOINT = "/v1/token";

    private static final String VAULT_URL_BASE = "https://%s.%s";

    /**
     * Create client using client credentials
     *
     * @param tenant
     * @param clientId
     * @param clientSecret
     * @param baseDomain
     */
    public BaseClient(String tenant, String clientId, String clientSecret, String baseDomain) {
        this.tenant = tenant;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.vaultUrl = String.format(VAULT_URL_BASE, tenant, baseDomain);
    }

    public void overrideVaultUrl(String url) {
        this.vaultUrl = url;
    }

    //TODO caching?
    public String getAuthToken() throws DevOpsSecretsVaultException {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
            AuthenticationBody auth = new AuthenticationBody(clientId, clientSecret);

            String authString = mapper.writeValueAsString(auth);

            URL authEndpoint = new URL(this.vaultUrl + TOKEN_ENDPOINT);
            HttpURLConnection conn = (HttpURLConnection) authEndpoint.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");

            OutputStream os = conn.getOutputStream();
            os.write(authString.getBytes());
            os.flush();

            HTTPCodeAnalyzer.validateResponseCode(conn.getResponseCode());
            AuthenticationResponse response = mapper.readValue(conn.getInputStream(), AuthenticationResponse.class);
            conn.disconnect();


            return response.getAccessToken();
        } catch (IOException e) {
            throw new DevOpsSecretsVaultException("Error authenticating with vault service", e);
        }
    }

    public String getEndpoint() {
        return vaultUrl;
    }
}
